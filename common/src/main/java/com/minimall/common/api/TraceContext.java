package com.minimall.common.api;

public final class TraceContext {

    public static final String TRACE_HEADER = "X-Trace-Id";
    public static final String REQUEST_HEADER = "X-Request-Id";

    private static final ThreadLocal<String> TRACE_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> REQUEST_ID = new ThreadLocal<>();

    private TraceContext() {
    }

    public static void setTraceId(String traceId) {
        TRACE_ID.set(traceId);
    }

    public static void setRequestId(String requestId) {
        REQUEST_ID.set(requestId);
    }

    public static String getTraceId() {
        return TRACE_ID.get();
    }

    public static String getRequestId() {
        return REQUEST_ID.get();
    }

    public static void clear() {
        TRACE_ID.remove();
        REQUEST_ID.remove();
    }
}
