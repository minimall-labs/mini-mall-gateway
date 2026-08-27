package com.minimall.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

public final class JwtSupport {

    public static final String HEADER = "Authorization";
    public static final String PREFIX = "Bearer ";
    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String USERNAME_HEADER = "X-Username";

    private final SecretKey key;
    private final long expireSeconds;

    public JwtSupport(String secret, long expireSeconds) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expireSeconds = expireSeconds;
    }

    public String createToken(long userId, String username) {
        Instant now = Instant.now();
        return Jwts.builder()
                .claims(Map.of("uid", userId, "uname", username))
                .subject(String.valueOf(userId))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expireSeconds)))
                .signWith(key)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long userId(Claims claims) {
        Object uid = claims.get("uid");
        if (uid instanceof Integer i) {
            return i.longValue();
        }
        if (uid instanceof Long l) {
            return l;
        }
        return Long.valueOf(claims.getSubject());
    }

    public String username(Claims claims) {
        Object uname = claims.get("uname");
        return uname == null ? null : String.valueOf(uname);
    }
}
