package com.shopstream.order_service.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    private final Key key;
    private final long expirationMillis;

    public JwtUtil(org.springframework.core.env.Environment env) {
        String secret = env.getProperty("security.jwt.secret", "sz5tLB0gUgGLJF0iIrTn/bizOyiwuIGej9+LIL/VITE=");
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMillis = Long.parseLong(env.getProperty("security.jwt.expiration-ms", "86400000")); // 1 day
    }

    public String generateToken(String username, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", roles);
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMillis);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException ex) {
            return false;
        }
    }

    public String getUsername(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        Object roles = claims.get("roles");
        if (roles instanceof java.util.List) {
            return ((java.util.List<?>) roles).stream().map(Object::toString).collect(Collectors.toList());
        }
        return java.util.List.of();
    }
}
