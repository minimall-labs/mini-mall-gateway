package com.minimall.gateway.config;

import com.minimall.common.security.JwtSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Bean
    public JwtSupport jwtSupport(
            @Value("${mini-mall.jwt.secret}") String secret,
            @Value("${mini-mall.jwt.expire-seconds:86400}") long expireSeconds) {
        return new JwtSupport(secret, expireSeconds);
    }
}
