package com.supremefuneralx.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT Token Provider - Generates, validates, and parses JWT tokens
 * 
 * Security Requirements:
 * - JWT_SECRET must be at least 32 characters (256 bits for HS256)
 * - Tokens are signed with HS256 algorithm
 * - Short-lived access tokens (default: 24 hours)
 * - Longer-lived refresh tokens (default: 7 days)
 */
@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret:}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:86400000}")
    private long jwtExpirationMs;

    @Value("${app.jwt.refresh-expiration:604800000}")
    private long jwtRefreshExpirationMs;

    /**
     * Generate JWT token from authentication
     * 
     * @param authentication Spring Security authentication object
     * @return JWT token string
     */
    public String generateToken(Authentication authentication) {
        if (jwtSecret == null || jwtSecret.isEmpty()) {
            throw new IllegalStateException("JWT_SECRET environment variable is not configured. " +
                    "Set JWT_SECRET with a strong random value (minimum 32 characters)");
        }

        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generate JWT token from username
     * 
     * @param username User identifier
     * @return JWT token string
     */
    public String generateTokenFromUsername(String username) {
        if (jwtSecret == null || jwtSecret.isEmpty()) {
            throw new IllegalStateException("JWT_SECRET environment variable is not configured");
        }

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Get username from JWT token
     * 
     * @param token JWT token string
     * @return Username extracted from token
     */
    public String getUsernameFromJwtToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * Validate JWT token
     * 
     * @param token JWT token string
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            if (jwtSecret == null || jwtSecret.isEmpty()) {
                log.error("JWT_SECRET is not configured");
                return false;
            }

            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (io.jsonwebtoken.security.SignatureException ex) {
            log.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (io.jsonwebtoken.MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (io.jsonwebtoken.ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
        } catch (io.jsonwebtoken.UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * Get expiration time from JWT token
     * 
     * @param token JWT token string
     * @return Expiration date
     */
    public Date getExpirationDateFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getExpiration();
    }

    /**
     * Check if token is expired
     * 
     * @param token JWT token string
     * @return true if token is expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception ex) {
            return true;
        }
    }
}
