package com.example.blog.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {


  private static String SECRET;
  private static final long ACCESS_TOKEN_EXPIRATION = 3600000;
  private static final long REFRESH_TOKEN_EXPIRATION = 1209600000;

  @Value("${secret}")
  public void setSecret(String value) {
    JwtUtil.SECRET = value;
  }

  public static String generateAccessToken(Long userId, String role){
    return Jwts.builder()
        .setSubject(String.valueOf(userId))
        .claim("role", role)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
        .signWith(getSigningKey())
        .compact();
  }

  public static String generateRefreshToken(Long userId){


    return Jwts.builder()
        .setSubject(String.valueOf(userId))
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
        .signWith(getSigningKey())
        .compact();
  }

  public static Claims parseToken(String token){
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private static Key getSigningKey(){
    return  Keys.hmacShaKeyFor(SECRET.getBytes());
  }
}

