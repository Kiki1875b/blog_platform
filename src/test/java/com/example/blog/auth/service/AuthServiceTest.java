package com.example.blog.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

import com.example.blog.auth.dto.RegisterRequestDTO;
import com.example.blog.auth.jwt.JwtService;
import com.example.blog.auth.jwt.TokenInvalidationService;
import com.example.blog.auth.user_details.CustomPrincipal;
import com.example.blog.auth.user_details.CustomUserDetails;
import com.example.blog.common.enumerated.MemberStatus;
import com.example.blog.common.exception.AuthException;
import com.example.blog.common.exception.ErrorCode;
import com.example.blog.domain.member.dto.MemberResponseDto;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.entity.MemberRole;
import com.example.blog.domain.member.repository.MemberRepository;
import com.example.blog.domain.refresh_token.RefreshTokenRepository;
import com.example.blog.domain.refresh_token.entity.RefreshToken;
import com.example.blog.mapper.MemberMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class AuthServiceTest {
  @Mock
  MemberRepository memberRepository;
  @Mock
  MemberMapper memberMapper;
  @Mock
  PasswordEncoder passwordEncoder;
  @Mock
  JwtService jwtService;
  @Mock
  TokenInvalidationService invalidationService;
  @Mock
  RefreshTokenRepository refreshTokenRepository;
  @Mock
  HttpServletRequest request;
  @Mock
  HttpServletResponse response;
  @InjectMocks AuthServiceImpl authService;
  Member m;
  @BeforeEach
  void setUp(){
    ReflectionTestUtils.setField(authService, "cookieSecure", false);
    m = new Member("email", "pwd", "nick", null, null,
        MemberStatus.ACTIVE, null, "name", MemberRole.USER);
    ReflectionTestUtils.setField(m, "id", UUID.randomUUID());
  }

  @Test
  @DisplayName("회원가입 성공 시 비밀번호 인코딩 및 저장 후 DTO 반환")
  void 회원가입_성공(){
    // given
    RegisterRequestDTO dto = new RegisterRequestDTO(
        "email@test.com", "rawPw", "rawPw", "nick", "name");
    given(memberRepository.findByEmail(dto.email())).willReturn(Optional.empty());
    Member entity = new Member(dto.email(), null, dto.nickname(), null, null,
        null, null, dto.name(), null);
    given(memberMapper.toEntity(dto)).willReturn(entity);
    given(passwordEncoder.encode("rawPw")).willReturn("ENCODED");

    ArgumentCaptor<Member> saved = ArgumentCaptor.forClass(Member.class);
    given(memberRepository.save(saved.capture())).willAnswer(i -> i.getArgument(0));

    MemberResponseDto expectedDto = new MemberResponseDto(
        UUID.randomUUID(), null, null, dto.email(), dto.nickname(),
        null, MemberStatus.ACTIVE, null, dto.name(), MemberRole.USER);
    given(memberMapper.toResponseDto(any(Member.class))).willReturn(expectedDto);

    // when

    MemberResponseDto res = authService.register(response, dto);
    // then

    Member savedMember = saved.getValue();
    assertThat(savedMember.getEmail()).isEqualTo("email@test.com");
    assertThat(savedMember.getPassword()).isEqualTo("ENCODED");
  }

  @Test
  @DisplayName("회원가입 비밀번호 불일치 실패")
  void 회원가입_비밀번호_불일치_실패(){
    // given
    RegisterRequestDTO dto = new RegisterRequestDTO(
        "email@email.com", "pwd","diffPwd", "nick", "name"
    );

    // when & then
    Assertions.assertThatThrownBy(()-> authService.register(response, dto)).isInstanceOf(
        AuthException.class);
  }

  @Test
  @DisplayName("회원가입시 중복 이메일 실패")
  void 회원가입시_중복_이메일_실패(){
    // given
    RegisterRequestDTO dto = new RegisterRequestDTO(
        "email@email.com", "pwd","pwd", "nick", "name"
    );
    Member member = mock(Member.class);
    given(memberRepository.findByEmail(dto.email())).willReturn(Optional.of(member));

    // when & then
    Assertions.assertThatThrownBy(()-> authService.register(response, dto)).isInstanceOf(
        AuthException.class);

  }


  @Test
  @DisplayName("로그아웃 성공")
  void 로그아웃_성공(){
    // given
    CustomPrincipal principal = new CustomPrincipal(UUID.randomUUID(), "test", "test", "ACTIVE");
    String accessToken = "accessToken";
    String refreshToken = "refreshToken";
    Date expiration = new Date();

    given(request.getHeader("Authorization")).willReturn("Bearer " + accessToken  );
    given(jwtService.extractExpiration(accessToken)).willReturn(expiration);
    given(request.getCookies()).willReturn(new Cookie[]{new Cookie("REFRESH_TOKEN", refreshToken)});
    given(jwtService.extractExpiration(refreshToken)).willReturn(expiration);

    // when
    authService.signOut(request, response, principal);

    // then
    verify(invalidationService).invalidate(eq(accessToken), any());
    verify(invalidationService).invalidate(eq(refreshToken), any());
    verify(refreshTokenRepository).deleteById(any());

  }

  @Test
  @DisplayName("리프레시 토큰 회전 성공")
  void 리프레시_토큰_회전_성공(){
    // given
    String refreshToken = "refreshToken";
    String newAccessToken = "newAccessToken";
    String newRefreshToken = "newRefreshToken";
    RefreshToken storedToken = new RefreshToken(m.getId(), refreshToken);
    Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);
    Claims c = mock(Claims.class);

    given(request.getCookies()).willReturn(new Cookie[]{refreshCookie});
    willDoNothing().given(jwtService).assertValid(any());
    willDoNothing().given(invalidationService).assertNotInvalid(any());
    given(jwtService.parse(any())).willReturn(c);
    given(jwtService.parse(any()).getSubject()).willReturn(m.getId().toString());
    given(refreshTokenRepository.findById(m.getId())).willReturn(Optional.of(storedToken));
    given(memberRepository.findById(m.getId())).willReturn(Optional.ofNullable(m));
    given(jwtService.generateAccessToken(m, m.getRole().name()))
        .willReturn(newAccessToken);
    given(jwtService.generateRefreshToken(m.getId()))
        .willReturn(newRefreshToken);
    given(refreshTokenRepository.save(any(RefreshToken.class))).willReturn(storedToken);
    given(jwtService.extractExpiration(refreshToken)).willReturn(new Date());

    ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);

    //when
    Map<String, Object> result = authService.refresh(request, response);

    //then
    assertThat(result).isNotEmpty();
    assertThat(result.get("accessToken")).isEqualTo(newAccessToken);
    verify(refreshTokenRepository).save(captor.capture());
  }

  @Test
  @DisplayName("리프레시_유효하지_않은_토큰_실패")
  void 리프레시_유효하지_않은_토큰_실패() {
    // given
    given(request.getCookies()).willReturn(new Cookie[]{new Cookie("REFRESH_TOKEN", "invalidToken")});
    willThrow(new AuthException(ErrorCode.INVALID_TOKEN_ERROR)).given(jwtService).assertValid("invalidToken");

    // when & then
    AuthException exception = assertThrows(AuthException.class, () -> authService.refresh(request, response));
    assertEquals(ErrorCode.INVALID_TOKEN_ERROR, exception.getErrorCode());
    verify(refreshTokenRepository, never()).save(any());
  }
}
