package com.minimall.common.api;

public record ApiResponse<T>(int code, String message, String traceId, T data) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(0, "ok", TraceContext.getTraceId(), data);
    }

    public static <T> ApiResponse<T> fail(int code, String message) {
        return new ApiResponse<>(code, message, TraceContext.getTraceId(), null);
    }
}
