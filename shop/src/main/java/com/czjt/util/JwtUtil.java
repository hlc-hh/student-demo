package com.czjt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

// Deleted:import static javax.crypto.Cipher.SECRET_KEY;

public class JwtUtil {

    private static final SecretKey KEY = Keys.hmacShaKeyFor("Y3pqdA==Y3pqdA==Y3pqdA==Y3pqdA==".getBytes(StandardCharsets.UTF_8));

    private static final long EXPIRATION_TIME = 12 * 3600 * 1000;


    private static SecretKey getSignKey() {
        return KEY;
    }

    public static String generateToken(String username, Integer userId) {
        return Jwts.builder()
                .claims(Map.of("userId", userId, "username", username))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignKey())
                .compact();
    }

    public static Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
