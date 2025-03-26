package com.example.blog.auth.handler;

import com.example.blog.auth.jwt.JwtUtil;
import com.example.blog.common.exception.AuthException;
import com.example.blog.common.exception.ErrorCode;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.repository.MemberRepository;
import com.example.blog.domain.refresh_token.RefreshToken;
import com.example.blog.domain.refresh_token.RefreshTokenRepository;
import com.example.blog.mapper.MemberMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FormLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final MemberRepository memberRepository;
  private final MemberMapper memberMapper;
  private final RefreshTokenRepository refreshTokenRepository;
  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    Member member = memberRepository.findByEmail(authentication.getName()).orElseThrow(() -> new AuthException(
        ErrorCode.USER_NOT_FOUND_BY_EMAIL));

    String accessToken = JwtUtil.generateAccessToken(member.getId(), "ROLE_USER");
    String refreshToken = JwtUtil.generateRefreshToken(member.getId());

    refreshTokenRepository.save(new RefreshToken(member.getId(), refreshToken));

    response.setHeader("Authorization", "Bearer " + accessToken);
    response.setHeader("Refresh-Token", refreshToken);

    response.setContentType("application/json");
    response.getWriter().write(new ObjectMapper().writeValueAsString(
        Map.of("id", member.getId(), "email", member.getEmail(), "name", member.getName(), "role", "ROLE_USER")
    ));
  }
}
