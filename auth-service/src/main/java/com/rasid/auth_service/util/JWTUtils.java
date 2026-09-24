package com.rasid.auth_service.util;

import com.rasid.auth_service.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.Function;

public class JWTUtils {
    private final String secret;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;


    public JWTUtils(@Value("${security.secret}") String secret,
                    @Value("${security.accessTokenExpiration}") long accessTokenExpiration,
                    @Value("${security.refreshTokenExpiration}") long refreshTokenExpiration) {
        this.secret = secret;
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    private Date getAccessTokenExpiration() {
        return new Date(System.currentTimeMillis() + accessTokenExpiration);
    }

    private Date getRefreshTokenExpiration() {
        return new Date(System.currentTimeMillis() + refreshTokenExpiration);
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateAccessToken(User user) {
        Map<String, Object> claims = new Hashtable<>();
        claims.put("role", user.getRole());
        return Jwts.builder()
                .signWith(getSecretKey())
                .expiration(getAccessTokenExpiration())
                .issuedAt(new Date(System.currentTimeMillis()))
                .subject(user.getEmail())
                .setClaims(claims)
                .compact();
    }

    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new Hashtable<>();
        claims.put("role", user.getRole());
        return Jwts.builder()
                .signWith(getSecretKey())
                .expiration(getRefreshTokenExpiration())
                .issuedAt(new Date(System.currentTimeMillis()))
                .subject(user.getEmail())
                .setClaims(claims)
                .compact();
    }

    public <T> T extractClaims(Claims claims, Function<Claims, T> claimResolver){
        return claimResolver.apply(claims);
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public Date extractExpirationDate(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

    public String extractRole(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpirationDate(token);
            return expiration.before(new Date(System.currentTimeMillis()));
        } catch (JwtException e) {
            return true;
        }
    }

}
