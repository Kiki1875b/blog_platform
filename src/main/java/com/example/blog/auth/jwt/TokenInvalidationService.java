package com.example.blog.auth.jwt;


import com.example.blog.auth.blacklist.Blacklist;
import com.example.blog.common.exception.AuthException;
import com.example.blog.common.exception.ErrorCode;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** 책임: 블랙리스트 기반 토큰 무효화/조회 */
@Service
@RequiredArgsConstructor
public class TokenInvalidationService {

  private final Blacklist blacklist;

  /** 블랙리스트 등록(만료시각까지) */
  public void invalidate(String token, Instant expiresAt) {
    blacklist.addToBlackList(token, expiresAt);
  }

  /** 블랙리스트 여부 */
  public boolean isInvalid(String token) {
    return blacklist.exists(token);
  }

  /** 무효 토큰인 경우 예외 */
  public void assertNotInvalid(String token) {
    if (isInvalid(token)) throw new AuthException(ErrorCode.INVALID_TOKEN_ERROR);
  }
}
