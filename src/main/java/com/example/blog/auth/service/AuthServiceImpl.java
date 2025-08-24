package com.example.blog.auth.service;

import com.example.blog.auth.jwt.JwtService;
import com.example.blog.common.enumerated.MemberStatus;
import com.example.blog.common.exception.AuthException;
import com.example.blog.common.exception.ErrorCode;
import com.example.blog.auth.dto.RegisterRequestDTO;
import com.example.blog.domain.member.MemberResponseDto;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.entity.MemberRole;
import com.example.blog.domain.member.repository.MemberRepository;
import com.example.blog.domain.refresh_token.RefreshToken;
import com.example.blog.domain.refresh_token.RefreshTokenRepository;
import com.example.blog.mapper.MemberMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

  private final MemberRepository memberRepository;
  private final MemberMapper memberMapper;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final RefreshTokenRepository refreshTokenRepository;
  private static final String ACCESS_COOKIE = "ACCESS_TOKEN";
  private static final String REFRESH_COOKIE = "REFRESH_TOKEN";
  @Override
  public MemberResponseDto register(HttpServletResponse response, RegisterRequestDTO registerDto) {

    if(!Objects.equals(registerDto.password(), registerDto.checkPassword())){
      throw new AuthException(ErrorCode.REGISTER_EXCEPTION);
    }

    Member member = memberMapper.toEntity(registerDto);
    member.updatePassword(passwordEncoder.encode(registerDto.password()));
    member.updateStatus(MemberStatus.ACTIVE);
    member.updateRole(MemberRole.USER);
    Member savedMember = memberRepository.save(member);

    return memberMapper.toResponseDto(savedMember);
  }

  @Override
  public void signOut(HttpServletRequest req, HttpServletResponse res) {
    // 1) 토큰 추출
    String accessToken = extractAccessTokenFromHeader(req).orElse(null);
    String refreshToken = extractCookie(req, REFRESH_COOKIE).orElse(null);

    // 2) 서버 측 무효화(블랙리스트 등) – 존재할 때만
    if (refreshToken != null) {
      jwtService.invalidateToken(refreshToken);
    }
    if (accessToken != null) {
      jwtService.invalidateToken(accessToken);
    }

    // 3) 쿠키 만료(둘 다 제거)
    ResponseCookie expiredAccess =
        ResponseCookie.from(ACCESS_COOKIE, "")
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
            .maxAge(Duration.ZERO) // Max-Age=0
            .build();

    ResponseCookie expiredRefresh =
        ResponseCookie.from(REFRESH_COOKIE, "")
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
            .maxAge(Duration.ZERO)
            .build();

    res.addHeader("Set-Cookie", expiredAccess.toString());
    res.addHeader("Set-Cookie", expiredRefresh.toString());
    res.setStatus(HttpServletResponse.SC_OK);
  }


  @Override
  public Map<String, Object>  refresh(HttpServletRequest request, HttpServletResponse response) {
    // 1. RefreshToken 추출 (쿠키 기반)
    String refreshToken = Arrays.stream(request.getCookies())
        .filter(c -> c.getName().equals("REFRESH_TOKEN"))
        .findFirst()
        .map(Cookie::getValue)
        .orElseThrow(() -> new AuthException(ErrorCode.INVALID_TOKEN_ERROR));

    // 2. RefreshToken 유효성 검증
    if (!jwtService.validateToken(refreshToken)) {
      throw new AuthException(ErrorCode.INVALID_TOKEN_ERROR);
    }

    // 3. DB(혹은 Redis)에서 RefreshToken 확인
    Long userId = Long.valueOf(jwtService.parseToken(refreshToken).getSubject());
    RefreshToken stored = refreshTokenRepository.findById(userId)
        .orElseThrow(() -> new AuthException(ErrorCode.INVALID_TOKEN_ERROR));

    if (!stored.getToken().equals(refreshToken)) {
      throw new AuthException(ErrorCode.INVALID_TOKEN_ERROR);
    }

    // 4. 새로운 AccessToken 발급
    Member member = memberRepository.findById(userId)
        .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));

    String newAccessToken = jwtService.generateAccessToken(member, member.getRole().name());

    return Map.of(
        "accessToken", newAccessToken,
        "expiresIn", 900  // 초 단위 (15분)
    );
  }


  // ===== Helpers =====
  private Optional<String> extractCookie(HttpServletRequest req, String name) {
    Cookie[] cookies = req.getCookies();
    if (cookies == null) return Optional.empty();
    for (Cookie c : cookies) {
      if (name.equals(c.getName())) {
        return Optional.ofNullable(c.getValue());
      }
    }
    return Optional.empty();
  }

  private Optional<String> extractAccessTokenFromHeader(HttpServletRequest req) {
    String authHeader = req.getHeader("Authorization");
    if (authHeader == null) return Optional.empty();
    final String prefix = "Bearer ";
    if (authHeader.startsWith(prefix) && authHeader.length() > prefix.length()) {
      return Optional.of(authHeader.substring(prefix.length()));
    }
    return Optional.empty();
  }
}
