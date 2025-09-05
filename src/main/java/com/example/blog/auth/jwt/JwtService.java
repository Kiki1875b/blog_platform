package com.example.blog.auth.jwt;


import com.example.blog.common.exception.AuthException;
import com.example.blog.common.exception.ErrorCode;
import com.example.blog.domain.member.entity.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 책임: JWT 생성/파싱/서명검증(만료 포함) - 블랙리스트 등 운영정책은 관여하지 않음
 */
@Component
@RequiredArgsConstructor
public class JwtService {

  @Value("${app.jwt.secret}")
  private String secret;

  @Value("${app.jwt.access-exp-ms:3600000}")
  private long accessExpMs;

  @Value("${app.jwt.refresh-exp-ms:1209600000}")
  private long refreshExpMs;

  /** 서명키 생성 */
  private Key key() {
    return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  /** AccessToken 생성 */
  public String generateAccessToken(Member member, String role) {
    return Jwts.builder()
        .setSubject(String.valueOf(member.getId()))
        .claim("role", role)
        .claim("email", member.getEmail())
        .claim("name", member.getName())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + accessExpMs))
        .signWith(key())
        .compact();
  }

  /** RefreshToken 생성 */
  public String generateRefreshToken(UUID userId) {
    return Jwts.builder()
        .setSubject(String.valueOf(userId))
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + refreshExpMs))
        .signWith(key())
        .compact();
  }

  /** 토큰 파싱(서명검증 포함) */
  public Claims parse(String token) {
    return Jwts.parserBuilder().setSigningKey(key()).build()
        .parseClaimsJws(token).getBody();
  }

  /** 만료 시각 추출(만료된 토큰도 허용) */
  public Date extractExpiration(String token) {
    try {
      return parse(token).getExpiration();
    } catch (ExpiredJwtException e) {
      return e.getClaims().getExpiration();
    }
  }

  /** 서명/만료 검증(블랙리스트는 여기서 다루지 않음) */
  public void assertValid(String token) {
    try {
      Date exp = parse(token).getExpiration();
      if (exp.before(new Date())) throw new AuthException(ErrorCode.INVALID_TOKEN_ERROR);
    } catch (Exception e) {
      throw new AuthException(ErrorCode.INVALID_TOKEN_ERROR);
    }
  }
}



