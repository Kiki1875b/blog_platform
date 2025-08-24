package com.example.blog.auth.handler;

import com.example.blog.auth.jwt.JwtService;
import com.example.blog.auth.oauth.CustomOAuth2User;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.repository.MemberRepository;
import com.example.blog.domain.refresh_token.RefreshToken;
import com.example.blog.domain.refresh_token.RefreshTokenRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class CustomOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
//
//  private final MemberRepository memberRepository;
//  private final RefreshTokenRepository refreshTokenRepository;
//  private final JwtService jwtService;
//
//  @Value("${app.client-url}")
//  private String CLIENT_URL;
//
//  @Value("${app.cookies.secure:true}")   // 로컬은 false, 운영은 true 권장
//  private boolean cookieSecure;
//  @Override
//  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//      Authentication authentication) throws IOException, ServletException {
//
//    CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
//    String email = oAuth2User.getAttribute("email");
//
//    Member member = memberRepository.findByEmail(email)
//        .orElseThrow(() -> new IllegalStateException("등록되지 않은 사용자입니다."));
//
//    String accessToken = jwtService.generateAccessToken(member, member.getRole().name());
//    String refreshToken = jwtService.generateRefreshToken(member.getId());
//
//
//    ResponseCookie access = ResponseCookie.from("ACCESS_TOKEN", accessToken)
//        .httpOnly(true)
//        .secure(cookieSecure)
//        .sameSite(cookieSecure ? "None" : "Lax")
//        .path("/")
//        .maxAge(Duration.ofMinutes(15))
//        .build();
//
//    ResponseCookie refresh = ResponseCookie.from("REFRESH_TOKEN", refreshToken)
//        .httpOnly(true)
//        .secure(cookieSecure)
//        .sameSite(cookieSecure ? "None" : "Lax")
//        .path("/")
//        .maxAge(Duration.ofDays(14))
//        .build();
//
//    response.addHeader("Set-Cookie", access.toString());
//    response.addHeader("Set-Cookie", refresh.toString());
//    response.sendRedirect("http://localhost:3000/");
//  }
//}

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
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
    String email = oAuth2User.getAttribute("email");

    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("등록되지 않은 사용자입니다."));

    // Refresh Token 발급 및 쿠키 저장
    String refreshToken = jwtService.generateRefreshToken(member.getId());

    ResponseCookie refresh = ResponseCookie.from("REFRESH_TOKEN", refreshToken)
        .httpOnly(true)
        .secure(cookieSecure)
        .sameSite(cookieSecure ? "None" : "Lax")
        .path("/api/auth/refresh")
        .maxAge(Duration.ofDays(14))
        .build();

    response.addHeader("Set-Cookie", refresh.toString());

    // Access Token은 여기서 주지 않고, 프론트는 redirect 후 /api/auth/refresh 호출로 발급 받음
    response.sendRedirect(CLIENT_URL + "/login/success");
  }
}
