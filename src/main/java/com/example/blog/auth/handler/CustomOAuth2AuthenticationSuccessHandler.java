package com.example.blog.auth.handler;

import com.example.blog.auth.jwt.JwtUtil;
import com.example.blog.common.enumerated.MemberStatus;
import com.example.blog.common.enumerated.Provider;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.repository.MemberRepository;
import com.example.blog.domain.refresh_token.RefreshToken;
import com.example.blog.domain.refresh_token.RefreshTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@RequiredArgsConstructor
public class CustomOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  private final MemberRepository memberRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    String email = oAuth2User.getAttribute("email");

    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("등록되지 않은 사용자입니다."));

    String accessToken = JwtUtil.generateAccessToken(member.getId(), "ROLE_USER");
    String refreshToken = JwtUtil.generateRefreshToken(member.getId());

    refreshTokenRepository.save(new RefreshToken(member.getId(), refreshToken));

    response.setHeader("Authorization", "Bearer " + accessToken);
    response.setHeader("Refresh-Token", refreshToken);
  }
}
