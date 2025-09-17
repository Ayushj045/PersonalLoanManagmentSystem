package com.nextGenZeta.LoanApplicationSystem.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    private String secretKey = "";
    private final Long expirationTime;

    public JwtUtil(@Value("${app.jwt.secret}") String secretKey,
                   @Value("${app.jwt.expirationMs}") Long expirationTime) {
        this.secretKey = secretKey;
        this.expirationTime = expirationTime;
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .claim("role", userDetails.getAuthorities().toString())
                .claims()
                .add(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .and()
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

}
//private final Key key;
//private final long expirationMs;
//
//public JwtUtil(@Value("${app.jwt.secret}") String secret,
//               @Value("${app.jwt.expirationMs}") long expirationMs) {
//    this.key = Keys.hmacShaKeyFor(secret.getBytes());
//    this.expirationMs = expirationMs;
//}
//
//// Generate token with username as subject
//public String generateToken(String username) {
//    Date now = new Date();
//    Date exp = new Date(now.getTime() + expirationMs);
//    return Jwts.builder()
//            .setSubject(username)
//            .setIssuedAt(now)
//            .setExpiration(exp)
//            .signWith(key, SignatureAlgorithm.HS256) // explicitly set algo
//            .compact();
//}
//
//// Extract username (subject)
//public String extractUsername(String token) {
//    return extractClaim(token, Claims::getSubject);
//}
//
//// Generic claim extractor
//public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//    final Claims claims = extractAllClaims(token);
//    return claimsResolver.apply(claims);
//}
//
//// Validate token against user details
//public boolean validateToken(String token, UserDetails userDetails) {
//    final String username = extractUsername(token);
//    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//}
//
//// Check expiration
//private boolean isTokenExpired(String token) {
//    return extractExpiration(token).before(new Date());
//}
//
//public Date extractExpiration(String token) {
//    return extractAllClaims(token).getExpiration();
//}
//
//// Extract claims
//private Claims extractAllClaims(String token) {
//    return Jwts.parserBuilder()
//            .setSigningKey(key)
//            .build()
//            .parseClaimsJws(token)
//            .getBody();
//}