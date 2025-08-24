package com.example.blog.auth.jwt;

import com.example.blog.auth.blacklist.Blacklist;
import com.example.blog.common.exception.AuthException;
import com.example.blog.common.exception.ErrorCode;
import com.example.blog.domain.member.entity.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtService {

  @Value("${app.jwt.secret}")
  private String SECRET;
  private final long ACCESS_TOKEN_EXPIRATION = 3600000;
  private final long REFRESH_TOKEN_EXPIRATION = 1209600000;
  private final Blacklist blacklist;

  public String generateAccessToken(Member member, String role){
    return Jwts.builder()
        .setSubject(String.valueOf(member.getId()))
        .claim("role", role)
        .claim("email", member.getEmail())
        .claim("name", member.getName())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
        .signWith(getSigningKey())
        .compact();
  }

  public String generateRefreshToken(Long userId) {
    return Jwts.builder()
        .setSubject(String.valueOf(userId))
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
        .signWith(getSigningKey())
        .compact();
  }

  public Claims parseToken(String token){
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  public boolean validateToken(String token){
    try{
      Claims claims = parseToken(token  );
      Date expiration = claims.getExpiration();
      return expiration.after(new Date());
    } catch (Exception e){
      throw new AuthException(ErrorCode.INVALID_TOKEN_ERROR);
    }
  }

  public void invalidateToken(String token){
    blacklist.addToBlackList(token, extractExpirationInstant(token));
  }

  public Date extractExpiration(String token) {
    try {
      return parseToken(token).getExpiration();
    } catch (ExpiredJwtException e) {
      return e.getClaims().getExpiration();
    }
  }

  public Instant extractExpirationInstant(String token) {
    return extractExpiration(token).toInstant();
  }
  private Key getSigningKey(){
    return  Keys.hmacShaKeyFor(SECRET.getBytes());
  }
}



