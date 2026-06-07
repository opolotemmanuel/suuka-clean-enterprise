package com.suuka.cleaning.auth.security;

import com.suuka.cleaning.users.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    private final SecretKey secretKey;
    private final long accessTokenMinutes;
    private final long refreshTokenDays;

    public JwtService(
            @Value("${suuka.security.jwt-secret}") String secret,
            @Value("${suuka.security.access-token-minutes}") long accessTokenMinutes,
            @Value("${suuka.security.refresh-token-days}") long refreshTokenDays
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenMinutes = accessTokenMinutes;
        this.refreshTokenDays = refreshTokenDays;
    }

    public String createAccessToken(User user) {
        Instant expiresAt = Instant.now().plus(accessTokenMinutes, ChronoUnit.MINUTES);
        return buildToken(user, expiresAt, "access");
    }

    public String createRefreshToken(User user) {
        Instant expiresAt = Instant.now().plus(refreshTokenDays, ChronoUnit.DAYS);
        return buildToken(user, expiresAt, "refresh");
    }

    public String subject(String token) {
        return claims(token).getSubject();
    }

    public boolean isValid(String token) {
        Claims claims = claims(token);
        return claims.getExpiration().after(new Date());
    }

    public String type(String token) {
        return claims(token).get("type", String.class);
    }

    private String buildToken(User user, Instant expiresAt, String type) {
        return Jwts.builder()
                .subject(user.getEmail())
                .claims(Map.of(
                        "userId", user.getId().toString(),
                        "role", user.getRole().name(),
                        "permissions", user.getPermissions().stream().map(Enum::name).toList(),
                        "branch", user.getBranch() == null ? "" : user.getBranch(),
                        "zone", user.getZone() == null ? "" : user.getZone(),
                        "type", type
                ))
                .issuedAt(new Date())
                .expiration(Date.from(expiresAt))
                .signWith(secretKey)
                .compact();
    }

    private Claims claims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
