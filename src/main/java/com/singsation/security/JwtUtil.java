package com.singsation.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;

@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        // Ensure key is at least 512 bits (64 bytes) for HS512
        if (keyBytes.length < 64) {
            // Pad with zeros if too short
            byte[] padded = new byte[64];
            System.arraycopy(keyBytes, 0, padded, 0, Math.min(keyBytes.length, 64));
            keyBytes = padded;
            System.out.println("⚠️ JWT secret was padded to 64 bytes for HS512");
        }
        System.out.println("🔑 Signing key length: " + keyBytes.length + " bytes");
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    public String generateToken(String username) {
        System.out.println("🔐 Generating token for: " + username);
        String token = Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact();
        System.out.println("✅ Token generated, length: " + token.length());
        return token;
    }
    
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            System.out.println("❌ Failed to parse token: " + e.getMessage());
            throw e;
        }
    }
    
    public boolean validateToken(String token) {
        try {
            System.out.println("🔍 Validating token...");
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            System.out.println("✅ Token is valid");
            return true;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            System.out.println("❌ Token expired: " + e.getMessage());
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            System.out.println("❌ Token malformed: " + e.getMessage());
        } catch (io.jsonwebtoken.security.SignatureException e) {
            System.out.println("❌ Invalid signature: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Token validation error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
        return false;
    }
}