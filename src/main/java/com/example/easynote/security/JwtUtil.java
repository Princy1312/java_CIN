package com.example.easynote.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private long expiration;

    private Key key() { return Keys.hmacShaKeyFor(secret.getBytes()); }

    public String generateToken(UserDetails u) {
        return Jwts.builder().setSubject(u.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key(), SignatureAlgorithm.HS256).compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> f) {
        return f.apply(Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody());
    }

    public boolean validateToken(String token, UserDetails u) {
        try {
            return extractUsername(token).equals(u.getUsername()) &&
                   !extractClaim(token, Claims::getExpiration).before(new Date());
        } catch (Exception e) { return false; }
    }
}
