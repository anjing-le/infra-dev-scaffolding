package com.anjing.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

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
}
