package com.anjing.context;

import com.anjing.model.request.GlobalRequestContext;

import java.util.Optional;

/**
 * Thread-local request context for controllers, services, logs, and remote calls.
 */
public final class GlobalRequestContextHolder {

    private static final ThreadLocal<GlobalRequestContext> HOLDER = new ThreadLocal<>();

    private GlobalRequestContextHolder() {
    }

    public static void set(GlobalRequestContext context) {
        HOLDER.set(context);
    }

    public static Optional<GlobalRequestContext> current() {
        return Optional.ofNullable(HOLDER.get());
    }

    public static String requestIdOrEmpty() {
        return current().map(GlobalRequestContext::getRequestId).orElse("");
    }

    public static String requestIdOrNull() {
        return current().map(GlobalRequestContext::getRequestId).orElse(null);
    }

    public static void clear() {
        HOLDER.remove();
    }
}
