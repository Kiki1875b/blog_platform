package com.example.blog.auth.handler;

import static com.example.blog.common.utils.CookieUtils.clearAllRefreshCookies;
import static com.example.blog.common.utils.CookieUtils.setRefreshCookie;

import com.example.blog.auth.jwt.JwtService;
import com.example.blog.auth.oauth.CustomOAuth2User;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.repository.MemberRepository;
import com.example.blog.domain.refresh_token.entity.RefreshToken;
import com.example.blog.domain.refresh_token.RefreshTokenRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CustomOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  private final MemberRepository memberRepository;
  private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

  @Value("${app.client-url}")
  private String CLIENT_URL;

  @Value("${app.cookies.secure:true}")
  private boolean cookieSecure;

  @Override
  @Transactional
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
    String email = oAuth2User.getAttribute("email");

    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("등록되지 않은 사용자입니다."));

    // Refresh Token 발급 및 쿠키 저장
    String refreshToken = jwtService.generateRefreshToken(member.getId());
    refreshTokenRepository.findById(member.getId()) // TODO : refresh token 은 id 를 member 와 공유해도 되는지?
        .ifPresentOrElse(
            token -> token.updateToken(refreshToken), // update 메서드 만들어두면 좋음
            () -> refreshTokenRepository.save(new RefreshToken(member.getId(), refreshToken))
        );

    clearAllRefreshCookies(response, cookieSecure, sameSite());
    setRefreshCookie(response, refreshToken, cookieSecure, sameSite());

    // Access Token은 여기서 주지 않고, 프론트는 redirect 후 /api/auth/refresh 호출로 발급 받음
    response.sendRedirect("http://localhost:3000" + "/");
  }

  private String sameSite() { return cookieSecure ? "None" : "Lax"; }
}
