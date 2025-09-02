package com.example.blog.auth.jwt;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

import com.example.blog.auth.jwt.JwtAuthenticationFilter;
import com.example.blog.auth.jwt.JwtService;
import com.example.blog.common.enumerated.MemberStatus;
import com.example.blog.common.enumerated.Provider;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.entity.MemberRole;
import com.example.blog.domain.member.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

  @Mock
  MemberRepository memberRepository;
  @Mock
  JwtService jwtService;
  @Mock
  FilterChain chain;
  @InjectMocks
  JwtAuthenticationFilter jwtFilter;

  @BeforeEach
  void setUp(){
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("Authorization 헤더가 없다면 인증을 시도하지 않는다")
  void 헤더_없을시_인증을_시도하지_않는다() throws ServletException, IOException {
    //given
    MockHttpServletRequest req = new MockHttpServletRequest();
    MockHttpServletResponse res = new MockHttpServletResponse();

    // when
    jwtFilter.doFilterInternal(req, res, chain);

    // then
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    then(chain).should().doFilter(req, res);
    then(jwtService).shouldHaveNoInteractions();
    then(memberRepository).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("유효한 토큰과 존재하는 회원이면 SecurityContext에 인증을 설정한다")
  void 유효토큰_회원존재_인증성공() throws ServletException, IOException {
    //given
    UUID userId = UUID.randomUUID();
    String token = "token.token.token";
    Member m = new Member("test", "test", "test", Provider.GOOGLE, "testid", MemberStatus.ACTIVE, null, "test", MemberRole.USER);
    Claims claims = mock(Claims.class);

    given(claims.getSubject()).willReturn(userId.toString());
    given(jwtService.parse(token)).willReturn(claims);
    given(memberRepository.findById(userId)).willReturn(Optional.of(m));

    MockHttpServletRequest req = new MockHttpServletRequest();
    MockHttpServletResponse res = new MockHttpServletResponse();

    req.addHeader("Authorization", "Bearer " + token);

    //when
    jwtFilter.doFilterInternal(req, res, chain);

    //then
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
  }

  @Test
  @DisplayName("유효한 토큰이지만 회원이 존재하지 않으면 인증을 설정하지 않는다")
  void 유효토큰_회원없음_미인증() throws ServletException, IOException {
    // given
    UUID userId = UUID.randomUUID();
    String token = "valid.jwt.token";

    Claims claims = mock(Claims.class);
    given(claims.getSubject()).willReturn(userId.toString());
    given(jwtService.parse(token)).willReturn(claims);
    given(memberRepository.findById(userId)).willReturn(Optional.empty());

    MockHttpServletRequest req = new MockHttpServletRequest();
    req.addHeader("Authorization", "Bearer " + token);
    MockHttpServletResponse res = new MockHttpServletResponse();

    // when
    jwtFilter.doFilterInternal(req, res, chain);

    // then
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    then(jwtService).should().parse(token);
    then(memberRepository).should().findById(userId);
  }


  @Test
  @DisplayName("토큰 파싱 중 예외가 발생하면 SecurityContext를 비우고 체인을 진행한다")
  void 토큰파싱_예외_컨텍스트초기화() throws ServletException, IOException {
    // given
    String token = "invalid.jwt.token";
    given(jwtService.parse(token)).willThrow(new RuntimeException("parse error"));

    // 미리 다른 인증이 들어있다고 가정(초기화 검증 목적)
    SecurityContextHolder.getContext().setAuthentication(
        new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("before", null)
    );

    MockHttpServletRequest req = new MockHttpServletRequest();
    req.addHeader("Authorization", "Bearer " + token);
    MockHttpServletResponse res = new MockHttpServletResponse();

    // when
    jwtFilter.doFilterInternal(req, res, chain);

    // then
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    then(jwtService).should().parse(token);
    then(memberRepository).shouldHaveNoInteractions();}

}
