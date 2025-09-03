package com.example.blog.auth.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.example.blog.auth.blacklist.Blacklist;
import com.example.blog.common.exception.AuthException;
import com.example.blog.common.exception.ErrorCode;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
public class TokenInvalidationServiceTest {
  @Mock
  private Blacklist blacklist;

  private TokenInvalidationService service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service = new TokenInvalidationService(blacklist);
  }

  @Test
  @DisplayName("invalidate 호출 시 블랙리스트에 토큰이 등록된다")
  void invalidate_호출시_블랙리스트에_등록된다() {
    // given
    String token = "dummy-token";
    Instant expiresAt = Instant.now().plusSeconds(60);

    // when
    service.invalidate(token, expiresAt);

    // then
    then(blacklist).should().addToBlackList(token, expiresAt);
  }

  @Test
  @DisplayName("isInvalid은 블랙리스트 조회 결과를 반환한다")
  void isInvalid은_블랙리스트_조회결과를_반환한다() {
    // given
    String token = "t1";
    given(blacklist.exists(token)).willReturn(true);

    // when
    boolean result = service.isInvalid(token);

    // then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("assertNotInvalid는 블랙리스트 토큰일 경우 예외를 던진다")
  void assertNotInvalid_블랙리스트면_예외() {
    // given
    String token = "invalid-token";
    given(blacklist.exists(token)).willReturn(true);

    // when & then
    assertThatThrownBy(() -> service.assertNotInvalid(token))
        .isInstanceOf(AuthException.class)
        .hasMessage(ErrorCode.INVALID_TOKEN_ERROR.getMessage());
  }

  @Test
  @DisplayName("assertNotInvalid는 정상 토큰일 경우 통과한다")
  void assertNotInvalid_정상토큰은_통과() {
    // given
    String token = "valid-token";
    given(blacklist.exists(token)).willReturn(false);

    // when & then (예외 없음)
    service.assertNotInvalid(token);
  }
}
