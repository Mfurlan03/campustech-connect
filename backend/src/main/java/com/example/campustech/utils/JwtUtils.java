package com.example.campustech.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {

    private final SecretKey SECRET_KEY;

    @Value("${jwt.expiration:86400000}") // Default: 1 day in milliseconds
    private Long EXPIRATION_MS;

    // ✅ Use a secure signing key (Ensures at least 256-bit key)
    public JwtUtils(@Value("${jwt.secret:default-secret-key}") String secret) {
        if (secret.length() < 32) { // Ensures at least 256-bit strength
            System.err.println("⚠️ WARNING: JWT Secret is too weak! Using a generated secure key.");
            this.SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        } else {
            this.SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        }
    }

    // ✅ Generate JWT token
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    // ✅ Create token with claims and expiration
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)  // 🔹 Uses a strong key
                .compact();
    }

    // ✅ Validate Token
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (ExpiredJwtException e) {
            System.err.println("❌ JWT Expired: " + e.getMessage());
            return false;
        } catch (MalformedJwtException | SignatureException e) {
            System.err.println("❌ Invalid JWT: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("❌ JWT Error: " + e.getMessage());
            return false;
        }
    }
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject); // The subject is the email
    }

    // ✅ Extract username from token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // ✅ Extract token from request header
    public String extractToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }

    // ✅ Check if token is expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // ✅ Extract expiration date from token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // ✅ Extract any claim from JWT
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // ✅ Extract all claims
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}