package com.example.blog.auth.jwt;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

import com.example.blog.common.exception.AuthException;
import com.example.blog.domain.member.entity.Member;
import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {
  private JwtService jwtService;
  private static final String TEST_SECRET = "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF";
  private static final long TEST_ACCESS_EXP_MS = 60_000L;
  private static final long TEST_REFRESH_EXP_MS = 1_209_600_000L;
  @BeforeEach
  void setUp(){
    jwtService = new JwtService();
    ReflectionTestUtils.setField(jwtService, "secret", TEST_SECRET);
    ReflectionTestUtils.setField(jwtService, "accessExpMs", TEST_ACCESS_EXP_MS);
    ReflectionTestUtils.setField(jwtService, "refreshExpMs", TEST_REFRESH_EXP_MS);
  }

  @Test
  @DisplayName("액세스 토큰을 생성하고 파싱할 수 있다")
  void 액세스_토큰을_생성하고_파싱할_수_있다(){
    //given
    UUID memberId = UUID.randomUUID();
    Member member = mock(Member.class);
    given(member.getId()).willReturn(memberId);
    given(member.getEmail()).willReturn("test@gmail.com");
    given(member.getName()).willReturn("test name");

    //when
    String accessToken = jwtService.generateAccessToken(member, "USER");
    Claims claims = jwtService.parse(accessToken);

    //then
    assertThat(claims.getSubject()).isEqualTo(memberId.toString());
    assertThat(claims.get("role")).isEqualTo("USER");
    assertThat(claims.get("email")).isEqualTo("test@gmail.com");
  }

  @Test
  @DisplayName("리프레시 토큰을 생성하고 파싱할 수 있다")
  void 리프레시_토큰을_생성하고_파싱할_수_있다(){
    // given
    UUID memberId = UUID.randomUUID();

    // when
    String refreshToken = jwtService.generateRefreshToken(memberId);
    Claims claims = jwtService.parse(refreshToken);

    // then
    assertThat(claims.getSubject()).isEqualTo(memberId.toString());
  }

  @Test
  @DisplayName("토큰에서 만료시간을 추출할 수 있다")
  void 토큰에서_만료시간을_추출할_수_있다(){
    //given
    UUID memberId = UUID.randomUUID();
    Member member = mock(Member.class);
    given(member.getId()).willReturn(memberId);
    given(member.getEmail()).willReturn("test@gmail.com");
    given(member.getName()).willReturn("test name");

    //when
    String token = jwtService.generateAccessToken(member, "USER");
    Date date = jwtService.extractExpiration(token);

    // then
    assertThat(date).isAfterOrEqualTo(new Date());
  }

  @Test
  @DisplayName("유효한 토큰은 검증을 통과한다")
  void 유효한_토큰은_검증을_통과한다(){
    // given
    UUID memberId = UUID.randomUUID();
    Member member = mock(Member.class);
    given(member.getId()).willReturn(memberId);
    given(member.getEmail()).willReturn("test@gmail.com");
    given(member.getName()).willReturn("test name");

    String token = jwtService.generateAccessToken(member, "USER");

    assertThatNoException().isThrownBy(() -> jwtService.assertValid(token));
  }

  @Test
  @DisplayName("만료시간이 지난 토큰은 검증을 실패한다")
  void 만료시간이_지난_토큰은_검증을_실패한다(){
    ReflectionTestUtils.setField(jwtService, "accessExpMs", -1_000L);
    // given
    UUID memberId = UUID.randomUUID();
    Member member = mock(Member.class);
    given(member.getId()).willReturn(memberId);
    given(member.getEmail()).willReturn("test@gmail.com");
    given(member.getName()).willReturn("test name");

    String token = jwtService.generateAccessToken(member, "USER");

    assertThatThrownBy(() -> jwtService.assertValid(token))
        .isInstanceOf(AuthException.class);
  }
}
