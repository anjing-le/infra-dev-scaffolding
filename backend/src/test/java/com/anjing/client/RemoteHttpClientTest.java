package com.anjing.client;

import com.anjing.config.properties.RemoteHttpClientProperties;
import com.anjing.context.GlobalRequestContextHolder;
import com.anjing.model.constants.RequestHeaderConstants;
import com.anjing.model.errorcode.RemoteErrorCode;
import com.anjing.model.exception.SystemException;
import com.anjing.model.request.GlobalRequestContext;
import com.anjing.model.response.APIResponse;
import com.anjing.model.response.PageResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class RemoteHttpClientTest {

    @AfterEach
    void tearDown() {
        GlobalRequestContextHolder.clear();
    }

    @Test
    void exchangeShouldPreserveNestedGenericResponseType() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        RemoteHttpClientProperties properties = properties();
        RemoteHttpClient client = new RemoteHttpClient(
                builder.build(),
                properties,
                new ConfiguredServiceEndpointResolver(properties),
                new NoopRemoteCallPolicy()
        );

        GlobalRequestContextHolder.set(GlobalRequestContext.builder()
                .requestId("rid-1")
                .traceId("tid-1")
                .tenantId("tenant-a")
                .userId("user-a")
                .locale("zh-CN")
                .timeZone("UTC")
                .build());

        server.expect(requestTo("http://inventory.local/api/items"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(RequestHeaderConstants.REQUEST_ID, "rid-1"))
                .andExpect(header(RequestHeaderConstants.TRACE_ID, "tid-1"))
                .andExpect(header(RequestHeaderConstants.TENANT_ID, "tenant-a"))
                .andRespond(withSuccess("""
                        {
                          "code": "0",
                          "message": "ok",
                          "data": {
                            "records": [{ "name": "alpha" }],
                            "current": 1,
                            "size": 1,
                            "total": 1
                          },
                          "timestamp": 1700000000000,
                          "requestId": "rid-1"
                        }
                        """, MediaType.APPLICATION_JSON));

        APIResponse<PageResult<ItemView>> response = client.exchange(
                RemoteHttpRequest.builder()
                        .serviceId("inventory")
                        .path("/api/items")
                        .build(),
                new ParameterizedTypeReference<APIResponse<PageResult<ItemView>>>() {
                }
        );

        assertEquals("0", response.getCode());
        assertEquals(1, response.getData().getRecords().size());
        assertInstanceOf(ItemView.class, response.getData().getRecords().get(0));
        assertEquals("alpha", response.getData().getRecords().get(0).getName());
        server.verify();
    }

    @Test
    void exchangeShouldApplyRemoteCallPolicyBeforeRequest() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        RemoteHttpClientProperties properties = properties();
        RemoteHttpClient client = new RemoteHttpClient(
                builder.build(),
                properties,
                new ConfiguredServiceEndpointResolver(properties),
                new RemoteCallPolicy() {
                    @Override
                    public void beforeCall(RemoteCallPolicyContext context) {
                        throw new SystemException(
                                "远程调用策略拒绝: " + context.targetService(),
                                RemoteErrorCode.REMOTE_CALL_CIRCUIT_BREAKER_OPEN
                        );
                    }
                }
        );

        SystemException error = assertThrows(
                SystemException.class,
                () -> client.exchange(
                        RemoteHttpRequest.builder()
                                .serviceId("inventory")
                                .path("/api/items")
                                .build(),
                        String.class
                )
        );

        assertEquals(RemoteErrorCode.REMOTE_CALL_CIRCUIT_BREAKER_OPEN, error.getErrorCode());
        server.verify();
    }

    @Test
    void exchangeShouldRecordRemoteCallPolicySuccess() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        RemoteHttpClientProperties properties = properties();
        RecordingRemoteCallPolicy policy = new RecordingRemoteCallPolicy();
        RemoteHttpClient client = new RemoteHttpClient(
                builder.build(),
                properties,
                new ConfiguredServiceEndpointResolver(properties),
                policy
        );

        server.expect(requestTo("http://inventory.local/api/items"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("ok", MediaType.TEXT_PLAIN));

        String response = client.exchange(
                RemoteHttpRequest.builder()
                        .serviceId("inventory")
                        .path("/api/items")
                        .build(),
                String.class
        );

        assertEquals("ok", response);
        assertEquals(1, policy.beforeCount);
        assertEquals(1, policy.successCount);
        assertEquals(0, policy.failureCount);
        assertEquals("GET", policy.context.method());
        assertEquals("inventory", policy.context.targetService());
        assertEquals("/api/items", policy.context.path());
        assertEquals("http://inventory.local/api/items", policy.context.url());
        server.verify();
    }

    @Test
    void configuredEndpointResolverShouldResolveServiceIdAndPath() {
        ConfiguredServiceEndpointResolver resolver = new ConfiguredServiceEndpointResolver(properties());

        assertEquals("http://inventory.local/api/items", resolver.resolveUrl("inventory", "/api/items"));
        assertEquals("http://inventory.local/api/items", resolver.resolveUrl("inventory", "api/items"));
        assertEquals("http://inventory.local", resolver.resolveUrl("inventory", null));
    }

    @Test
    void configuredEndpointResolverShouldRejectUnknownService() {
        ConfiguredServiceEndpointResolver resolver = new ConfiguredServiceEndpointResolver(properties());

        SystemException error = assertThrows(
                SystemException.class,
                () -> resolver.resolveUrl("missing-service", "/api/items")
        );
        assertEquals(RemoteErrorCode.REMOTE_CALL_PARAM_ERROR, error.getErrorCode());
    }

    private RemoteHttpClientProperties properties() {
        RemoteHttpClientProperties properties = new RemoteHttpClientProperties();
        properties.getServiceBaseUrls().put("inventory", "http://inventory.local");
        properties.setDefaultCallerId("infra-dev-scaffolding-test");
        return properties;
    }

    static class ItemView {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    static class RecordingRemoteCallPolicy implements RemoteCallPolicy {
        private int beforeCount;
        private int successCount;
        private int failureCount;
        private RemoteCallPolicyContext context;

        @Override
        public void beforeCall(RemoteCallPolicyContext context) {
            this.beforeCount++;
            this.context = context;
        }

        @Override
        public void afterSuccess(RemoteCallPolicyContext context) {
            this.successCount++;
            this.context = context;
        }

        @Override
        public void afterFailure(RemoteCallPolicyContext context, RuntimeException exception) {
            this.failureCount++;
            this.context = context;
        }
    }
}
