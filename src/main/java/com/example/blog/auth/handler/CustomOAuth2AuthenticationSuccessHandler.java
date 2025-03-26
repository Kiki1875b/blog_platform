package com.example.blog.auth.handler;

import com.example.blog.auth.jwt.JwtUtil;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.repository.MemberRepository;
import com.example.blog.domain.refresh_token.RefreshToken;
import com.example.blog.domain.refresh_token.RefreshTokenRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  private final MemberRepository memberRepository;
  private final RefreshTokenRepository refreshTokenRepository;

  @Value("${client-url}")
  private String CLIENT_URL;
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

    String encodedName = URLEncoder.encode(member.getName(), StandardCharsets.UTF_8);
    String encodedEmail = URLEncoder.encode(member.getEmail(), StandardCharsets.UTF_8);
    String encodedRole = URLEncoder.encode("USER", StandardCharsets.UTF_8);

    String redirectUri = CLIENT_URL + "/oauth2/redirect"
        + "?token=" + accessToken
        + "&refreshToken=" + refreshToken
        + "&id=" + member.getId()
        + "&name=" + encodedName
        + "&email=" + encodedEmail
        + "&role=" + encodedRole;

    response.sendRedirect(redirectUri);
  }
}
