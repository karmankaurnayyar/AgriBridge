package com.agribridge.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT issuing/parsing foundation for AgriBridge.
 *
 * Week 2 status: this utility can generate a signed token at login and
 * validate/parse a token's claims. Wiring a JwtAuthenticationFilter into the
 * Spring Security filter chain to enforce token-based authorization on every
 * protected endpoint is planned for Week 3 (see docs/roadmap.md and
 * docs/architecture.md, Section "Security"). For Week 2, protected endpoints
 * are secured through Spring Security's default authenticated-session model
 * rather than JWT, so the Farm Management prototype is functionally secured
 * even though full JWT filter-chain enforcement is not yet wired in.
 */
@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expirationMillis;

    public JwtUtil(
            @Value("${agribridge.jwt.secret:dev-only-placeholder-secret-change-me-please-32chars}") String secret,
            @Value("${agribridge.jwt.expiration-ms:3600000}") long expirationMillis) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = expirationMillis;
    }

    public String generateToken(String subjectEmail, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMillis);
        return Jwts.builder()
                .subject(subjectEmail)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public long getExpirationSeconds() {
        return expirationMillis / 1000;
    }
}
