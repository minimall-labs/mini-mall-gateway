package com.minimall.common.web;

import com.minimall.common.api.TraceContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestId = request.getHeader(TraceContext.REQUEST_HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString().replace("-", "");
        }
        String traceId = request.getHeader(TraceContext.TRACE_HEADER);
        if (traceId == null || traceId.isBlank()) {
            traceId = requestId;
        }
        MDC.put("request_id", requestId);
        MDC.put("trace_id", traceId);
        MDC.put("method", request.getMethod());
        MDC.put("path", request.getRequestURI());
        TraceContext.setRequestId(requestId);
        TraceContext.setTraceId(traceId);
        response.setHeader(TraceContext.REQUEST_HEADER, requestId);
        response.setHeader(TraceContext.TRACE_HEADER, traceId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("request_id");
            MDC.remove("trace_id");
            MDC.remove("method");
            MDC.remove("path");
            TraceContext.clear();
        }
    }
}
