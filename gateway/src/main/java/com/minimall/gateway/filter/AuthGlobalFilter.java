package com.minimall.gateway.filter;

import com.minimall.common.api.TraceContext;
import com.minimall.common.security.JwtSupport;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private static final List<String> WHITE_LIST = List.of(
            "/api/auth/register",
            "/api/auth/login",
            "/api/search/**",
            "/api/marketing/coupons/templates",
            "/api/consumer/health",
            "/api/consumer/home",
            "/api/consumer/search/**",
            "/api/consumer/auth/**",
            "/api/consumer/shops",
            "/api/consumer/shops/**",
            "/api/consumer/products/*/summary",
            "/api/workbench/health",
            "/api/workbench/modules",
            "/api/workbench/micro-app-routes",
            "/open/v1/health",
            "/open/v1/ping",
            "/open/v1/apis",
            "/open/v1/debug/**",
            "/open/v1/products/**",
            "/ws/im/**",
            "/api/id/**",
            "/actuator/health",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/webjars/**",
            "/docs/**"
    );

    private final JwtSupport jwtSupport;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public AuthGlobalFilter(JwtSupport jwtSupport) {
        this.jwtSupport = jwtSupport;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Browser CORS preflight must not require JWT.
        if (HttpMethod.OPTIONS.equals(exchange.getRequest().getMethod())) {
            return chain.filter(exchange);
        }

        String path = exchange.getRequest().getURI().getPath();
        String requestId = exchange.getRequest().getHeaders().getFirst(TraceContext.REQUEST_HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString().replace("-", "");
        }
        String traceId = exchange.getRequest().getHeaders().getFirst(TraceContext.TRACE_HEADER);
        if (traceId == null || traceId.isBlank()) {
            traceId = requestId;
        }

        // 仅向下游请求注入；响应头由下游回写。勿在 proxy 前写 response，否则会与下游叠加成重复头。
        ServerHttpRequest.Builder requestBuilder = exchange.getRequest().mutate()
                .header(TraceContext.REQUEST_HEADER, requestId)
                .header(TraceContext.TRACE_HEADER, traceId);

        if (HttpMethod.GET.equals(exchange.getRequest().getMethod())
                && pathMatcher.match("/api/products/**", path)) {
            return chain.filter(exchange.mutate().request(requestBuilder.build()).build());
        }

        if (isWhiteListed(path)) {
            return chain.filter(exchange.mutate().request(requestBuilder.build()).build());
        }

        String auth = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith(JwtSupport.PREFIX)) {
            return unauthorized(exchange, requestId, traceId, "missing bearer token");
        }
        try {
            Claims claims = jwtSupport.parse(auth.substring(JwtSupport.PREFIX.length()));
            Long userId = jwtSupport.userId(claims);
            String username = jwtSupport.username(claims);
            ServerHttpRequest request = requestBuilder
                    .header(JwtSupport.USER_ID_HEADER, String.valueOf(userId))
                    .header(JwtSupport.USERNAME_HEADER, username == null ? "" : username)
                    .build();
            return chain.filter(exchange.mutate().request(request).build());
        } catch (Exception ex) {
            return unauthorized(exchange, requestId, traceId, "invalid token");
        }
    }

    private boolean isWhiteListed(String path) {
        return WHITE_LIST.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private Mono<Void> unauthorized(
            ServerWebExchange exchange, String requestId, String traceId, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().getHeaders().set(TraceContext.REQUEST_HEADER, requestId);
        exchange.getResponse().getHeaders().set(TraceContext.TRACE_HEADER, traceId);
        String body = "{\"code\":401,\"message\":\"" + message + "\",\"traceId\":\"" + traceId + "\",\"data\":null}";
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
