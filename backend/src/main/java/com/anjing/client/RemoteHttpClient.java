package com.anjing.client;

import com.anjing.config.properties.RemoteHttpClientProperties;
import com.anjing.model.errorcode.RemoteErrorCode;
import com.anjing.model.exception.SystemException;
import com.anjing.util.RemoteCallWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Central outbound HTTP adapter.
 *
 * <p>It keeps service-to-service headers, timeout defaults, retry behavior, and
 * audit logs consistent before the scaffold evolves into multiple services.</p>
 */
@Component
@RequiredArgsConstructor
public class RemoteHttpClient {

    private final RestClient remoteRestClient;
    private final RemoteHttpClientProperties properties;

    public <R> R get(String url, Class<R> responseType) {
        return exchange(RemoteHttpRequest.builder()
                .method(HttpMethod.GET)
                .url(url)
                .build(), responseType);
    }

    public <T, R> R post(String url, T body, Class<R> responseType) {
        return exchange(RemoteHttpRequest.builder()
                .method(HttpMethod.POST)
                .url(url)
                .body(body)
                .build(), responseType);
    }

    public <R> R exchange(RemoteHttpRequest request, Class<R> responseType) {
        validateRequest(request, responseType);

        Map<String, Object> descriptor = describeRequest(request);
        return RemoteCallWrapper.callWithRetry(
                ignored -> doExchange(request, responseType),
                descriptor,
                remoteCallName(request),
                resolveRetryCount(request),
                request.isCheckResponse(),
                resolveRetryInterval(request)
        );
    }

    private <R> R doExchange(RemoteHttpRequest request, Class<R> responseType) {
        try {
            RestClient.RequestBodySpec spec = remoteRestClient
                    .method(resolveMethod(request))
                    .uri(request.getUrl());

            buildHeaders(request).forEach((name, value) -> spec.header(name, value));

            RestClient.ResponseSpec responseSpec = shouldSendBody(request)
                    ? spec.body(request.getBody()).retrieve()
                    : spec.retrieve();

            return responseSpec.body(responseType);
        } catch (ResourceAccessException e) {
            throw new SystemException(
                    "远程 HTTP 调用网络异常: " + remoteCallName(request),
                    e,
                    RemoteErrorCode.REMOTE_CALL_NETWORK_ERROR
            );
        } catch (RestClientResponseException e) {
            throw new SystemException(
                    String.format("远程 HTTP 响应异常: %s, status=%s", remoteCallName(request), e.getStatusCode().value()),
                    e,
                    remoteErrorCode(e.getStatusCode())
            );
        } catch (RestClientException e) {
            throw new SystemException(
                    "远程 HTTP 调用失败: " + remoteCallName(request),
                    e,
                    RemoteErrorCode.REMOTE_CALL_FAILED
            );
        }
    }

    private Map<String, String> buildHeaders(RemoteHttpRequest request) {
        Map<String, String> headers = RemoteCallWrapper.serviceCallHeaders(resolveCallerId(request));
        if (request.getHeaders() == null) {
            return headers;
        }

        request.getHeaders().forEach((name, value) -> {
            if (StringUtils.hasText(name) && StringUtils.hasText(value)) {
                headers.put(name, value);
            }
        });
        return headers;
    }

    private Map<String, Object> describeRequest(RemoteHttpRequest request) {
        Map<String, Object> descriptor = new LinkedHashMap<>();
        descriptor.put("method", resolveMethod(request).name());
        descriptor.put("targetService", resolveTargetService(request));
        descriptor.put("url", sanitizedUrl(request.getUrl()));
        descriptor.put("callerId", resolveCallerId(request));
        return descriptor;
    }

    private void validateRequest(RemoteHttpRequest request, Class<?> responseType) {
        if (request == null) {
            throw new SystemException("远程 HTTP 请求不能为空", RemoteErrorCode.REMOTE_CALL_PARAM_ERROR);
        }
        if (!StringUtils.hasText(request.getUrl())) {
            throw new SystemException("远程 HTTP URL 不能为空", RemoteErrorCode.REMOTE_CALL_PARAM_ERROR);
        }
        validateAbsoluteUrl(request.getUrl());
        if (responseType == null) {
            throw new SystemException("远程 HTTP 响应类型不能为空", RemoteErrorCode.REMOTE_CALL_PARAM_ERROR);
        }
    }

    private void validateAbsoluteUrl(String url) {
        try {
            URI uri = URI.create(url);
            if (!StringUtils.hasText(uri.getScheme()) || !StringUtils.hasText(uri.getAuthority())) {
                throw new IllegalArgumentException("Remote HTTP URL must be absolute");
            }
        } catch (Exception e) {
            throw new SystemException("远程 HTTP URL 必须是绝对地址", e, RemoteErrorCode.REMOTE_CALL_PARAM_ERROR);
        }
    }

    private HttpMethod resolveMethod(RemoteHttpRequest request) {
        return request.getMethod() == null ? HttpMethod.GET : request.getMethod();
    }

    private String resolveCallerId(RemoteHttpRequest request) {
        return StringUtils.hasText(request.getCallerId())
                ? request.getCallerId()
                : properties.getDefaultCallerId();
    }

    private int resolveRetryCount(RemoteHttpRequest request) {
        return request.getRetryCount() == null ? properties.getDefaultRetryCount() : request.getRetryCount();
    }

    private long resolveRetryInterval(RemoteHttpRequest request) {
        return request.getRetryIntervalMs() == null
                ? properties.getDefaultRetryIntervalMs()
                : request.getRetryIntervalMs();
    }

    private boolean shouldSendBody(RemoteHttpRequest request) {
        return request.getBody() != null
                && (HttpMethod.POST.equals(resolveMethod(request))
                || HttpMethod.PUT.equals(resolveMethod(request))
                || HttpMethod.PATCH.equals(resolveMethod(request)));
    }

    private String remoteCallName(RemoteHttpRequest request) {
        return resolveMethod(request).name() + " " + resolveTargetService(request);
    }

    private String resolveTargetService(RemoteHttpRequest request) {
        if (StringUtils.hasText(request.getTargetService())) {
            return request.getTargetService();
        }

        try {
            URI uri = URI.create(request.getUrl());
            return StringUtils.hasText(uri.getHost()) ? uri.getHost() : "unknown-service";
        } catch (Exception ignored) {
            return "unknown-service";
        }
    }

    private String sanitizedUrl(String url) {
        try {
            URI uri = URI.create(url);
            String queryMarker = StringUtils.hasText(uri.getQuery()) ? "?..." : "";
            return URI.create(uri.getScheme() + "://" + uri.getAuthority() + uri.getPath()).toString() + queryMarker;
        } catch (Exception ignored) {
            return url;
        }
    }

    private RemoteErrorCode remoteErrorCode(HttpStatusCode statusCode) {
        int status = statusCode.value();
        if (status == 408 || status == 504) {
            return RemoteErrorCode.REMOTE_CALL_TIMEOUT;
        }
        if (status == 429 || status == 502 || status == 503) {
            return RemoteErrorCode.REMOTE_CALL_NETWORK_ERROR;
        }
        return RemoteErrorCode.REMOTE_RESPONSE_STATUS_FAILED;
    }
}
