package com.anjing.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Defaults for outbound HTTP calls to future internal services or third-party APIs.
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.remote-http")
public class RemoteHttpClientProperties {

    /**
     * Default caller id propagated through X-Caller-Id.
     */
    private String defaultCallerId = "infra-dev-scaffolding";

    /**
     * Connection timeout for outbound HTTP calls.
     */
    private int connectTimeoutMs = 3000;

    /**
     * Read timeout for outbound HTTP calls.
     */
    private int readTimeoutMs = 5000;

    /**
     * Default retry count for calls using RemoteHttpClient.
     */
    private int defaultRetryCount = 0;

    /**
     * Default retry interval in milliseconds.
     */
    private long defaultRetryIntervalMs = 1000L;

    /**
     * Logical service id to base URL mapping.
     *
     * <p>Keep service addresses in configuration so business code can call
     * RemoteHttpRequest with serviceId + path instead of scattering absolute URLs.</p>
     */
    private Map<String, String> serviceBaseUrls = new LinkedHashMap<>();
}
