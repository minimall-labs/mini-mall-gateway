package com.minimall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Allow browser workbench (shell :5100 and micro-apps) to call Gateway :8080.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(List.of(
                "http://127.0.0.1:*",
                "http://localhost:*",
                "http://10.0.2.2:*",
                // LAN / phone debugging (App WebView opens H5 via http://192.168.x.x:5201)
                "http://*",
                "http://www.mini-mall.localhost",
                "http://mini-mall.localhost",
                "http://*.mini-mall.localhost",
                "http://merchant.mini-mall.localhost",
                "http://h5.mini-mall.localhost",
                "http://app.mini-mall.localhost",
                "http://creator.mini-mall.localhost"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("X-Trace-Id", "Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }
}
