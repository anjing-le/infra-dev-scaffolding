package com.anjing.client;

import com.anjing.config.properties.RemoteHttpClientProperties;
import com.anjing.context.GlobalRequestContextHolder;
import com.anjing.model.constants.RequestHeaderConstants;
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
        RemoteHttpClient client = new RemoteHttpClient(builder.build(), properties());

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
}
