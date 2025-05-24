package com.zicket.zicket.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${SECRET_KEY}")
    private String secretKey;

    public String extractUsername(String token)
    {
        Claims claims=extractAllClaims(token);
        return claims.getSubject();
    }

    public Date extractExpiration(String token)
    {
        return extractAllClaims(token).getExpiration();
    }

    private SecretKey getSigningKey()
    {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(String username)
    {
        Map<String, Object> claims=new HashMap<>();
        return createToken(claims, username);
    }

    public Claims extractAllClaims(String token)
    {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Boolean isTokenExpired(String token)
    {
        return extractExpiration(token).before(new Date());
    }

    public String createToken(Map<String, Object> claims, String username)
    {
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .header().empty().add("typ", "JWT")
                .and()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+1000*60))
                .signWith(getSigningKey())
                .compact();
    }

    public Boolean validToken(String token)
    {
        return !isTokenExpired(token);
    }
}
