package com.goorm.membership.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    private final SecretKey secretKey;
    private final long expirationMs;

    // 키 값, 만료 시간 값을 받아 초기화
    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration-ms}") long expirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    // 이메일을 담은 토큰 생성
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())                                               // 발급 시간
                .expiration(new Date(System.currentTimeMillis() + expirationMs))    // 만료 시간
                .signWith(secretKey)                                                // 서명
                .compact();
    }

    // 토큰 파싱해서 payload 반환
    private Claims parsePayload(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 토큰에서 이메일 추출
    public String extractEmail(String token) {
        return parsePayload(token).getSubject();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            parsePayload(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
