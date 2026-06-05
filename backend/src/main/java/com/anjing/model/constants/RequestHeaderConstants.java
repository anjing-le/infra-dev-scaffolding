package com.anjing.model.constants;

/**
 * Request header names used by frontend, backend, and future service calls.
 */
public final class RequestHeaderConstants {

    public static final String REQUEST_ID = "X-Request-Id";
    public static final String TRACE_ID = "X-Trace-Id";
    public static final String TENANT_ID = "X-Tenant-Id";
    public static final String USER_ID = "X-User-Id";
    public static final String USER_NAME = "X-User-Name";
    public static final String USER_ROLES = "X-User-Roles";
    public static final String CALLER_ID = "X-Caller-Id";
    public static final String TIME_ZONE = "X-Time-Zone";
    public static final String ACCEPT_LANGUAGE = "Accept-Language";

    private RequestHeaderConstants() {
    }
}
