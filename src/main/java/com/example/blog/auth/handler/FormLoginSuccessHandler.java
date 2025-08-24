package com.example.blog.auth.handler;

import com.example.blog.auth.jwt.JwtService;
import com.example.blog.auth.user_details.CustomUserDetails;
import com.example.blog.common.exception.AuthException;
import com.example.blog.common.exception.ErrorCode;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.repository.MemberRepository;
import com.example.blog.domain.refresh_token.RefreshToken;
import com.example.blog.domain.refresh_token.RefreshTokenRepository;
import com.example.blog.mapper.MemberMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

//
//@Component
//@RequiredArgsConstructor
//public class FormLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
//
//
//  private final RefreshTokenRepository refreshTokenRepository;
//  private final JwtService jwtService;
//  @Override
//  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//      Authentication authentication) throws IOException {
//    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//    Member member = userDetails.getMember();
//
//    String accessToken = jwtService.generateAccessToken(member, member.getRole().name());
//    String refreshToken = jwtService.generateRefreshToken(member.getId());
//
//    ResponseCookie access = ResponseCookie.from("ACCESS_TOKEN", accessToken)
//        .httpOnly(true).secure(true).sameSite("None").path("/")
//        .maxAge(Duration.ofMinutes(15)).build();
//
//    ResponseCookie refresh = ResponseCookie.from("REFRESH_TOKEN", refreshToken)
//        .httpOnly(true).secure(true).sameSite("None").path("/")
//        .maxAge(Duration.ofDays(14)).build();
//
//    response.addHeader("Set-Cookie", access.toString());
//    response.addHeader("Set-Cookie", refresh.toString());
//
//    // 응답 바디엔 최소한의 유저 정보
//    response.setStatus(HttpServletResponse.SC_OK);
//    response.setContentType("application/json");
//    response.getWriter().write(new ObjectMapper().writeValueAsString(
//        Map.of("id", member.getId(),
//            "email", member.getEmail(),
//            "name", member.getName(),
//            "role", member.getRole())
//    ));
//  }
//
//}
@Component
@RequiredArgsConstructor
public class FormLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final JwtService jwtService;
  private final RefreshTokenRepository refreshTokenRepository;

  @Override
  @Transactional
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    Member member = userDetails.getMember();

    // 토큰 발급
    String accessToken = jwtService.generateAccessToken(member, member.getRole().name());
    String refreshToken = jwtService.generateRefreshToken(member.getId());

    refreshTokenRepository.findById(member.getId()) // TODO : refresh token 은 id 를 member 와 공유해도 되는지?
        .ifPresentOrElse(
            token -> token.updateToken(refreshToken), // update 메서드 만들어두면 좋음
            () -> refreshTokenRepository.save(new RefreshToken(member.getId(), refreshToken))
        );

    // Refresh Token 쿠키 저장
    ResponseCookie refresh = ResponseCookie.from("REFRESH_TOKEN", refreshToken)
        .httpOnly(true)
        .secure(true)
        .sameSite("None") // FE/BE 분리 환경이면 None
        .path("/")
        .maxAge(Duration.ofDays(14))
        .build();

    response.addHeader("Set-Cookie", refresh.toString());

    // 응답 Body에는 Access Token만 내려줌
    response.setStatus(HttpServletResponse.SC_OK);
//    response.setContentType("application/json");
//    response.getWriter().write(new ObjectMapper().writeValueAsString(
//        Map.of(
//            "accessToken", accessToken,
//            "expiresIn", 900
//        )
//    ));
  }
}
