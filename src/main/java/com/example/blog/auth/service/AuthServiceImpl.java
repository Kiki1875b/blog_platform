package com.example.blog.auth.service;

import static com.example.blog.common.utils.CookieUtils.clearAllRefreshCookies;
import static com.example.blog.common.utils.CookieUtils.setRefreshCookie;

import com.example.blog.auth.jwt.JwtService;
import com.example.blog.auth.jwt.TokenInvalidationService;
import com.example.blog.common.aws.s3.S3Service;
import com.example.blog.common.enumerated.MemberStatus;
import com.example.blog.common.exception.AuthException;
import com.example.blog.common.exception.ErrorCode;
import com.example.blog.auth.dto.RegisterRequestDTO;
import com.example.blog.common.utils.CookieUtils;
import com.example.blog.domain.member.dto.MemberResponseDto;
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
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
//
//
//@Service
//@RequiredArgsConstructor
//public class AuthServiceImpl implements AuthService{
//
//  private final MemberRepository memberRepository;
//  private final MemberMapper memberMapper;
//  private final PasswordEncoder passwordEncoder;
//  private final JwtService jwtService;
//  private final S3Service s3Service;
//  private final RefreshTokenRepository refreshTokenRepository;
//  private static final String ACCESS_COOKIE = "ACCESS_TOKEN";
//  private static final String REFRESH_COOKIE = "REFRESH_TOKEN";
//
//  @Value("${app.cookies.secure:true}")
//  private boolean cookieSecure;
//  private String sameSite() { return cookieSecure ? "None" : "Lax"; }
//  @Override
//  public MemberResponseDto register(HttpServletResponse response, RegisterRequestDTO registerDto) {
//
//    if(!Objects.equals(registerDto.password(), registerDto.checkPassword())){
//      throw new AuthException(ErrorCode.REGISTER_EXCEPTION);
//    }
//
//    Member member = memberMapper.toEntity(registerDto);
//    member.updatePassword(passwordEncoder.encode(registerDto.password()));
//    member.updateStatus(MemberStatus.ACTIVE);
//    member.updateRole(MemberRole.USER);
//    Member savedMember = memberRepository.save(member);
//
//    return memberMapper.toResponseDto(savedMember);
//  }
//
//  @Override
//  public void signOut(HttpServletRequest req, HttpServletResponse res, PrincipalMember member) {
//    // 1) 토큰 추출
//    String accessToken = extractAccessTokenFromHeader(req).orElse(null);
//    String refreshToken = extractCookie(req, REFRESH_COOKIE).orElse(null);
//
//    // 2) 서버 측 무효화(블랙리스트 등) – 존재할 때만
//    if (refreshToken != null) {
//      jwtService.invalidateToken(refreshToken);
//    }
//    if (accessToken != null) {
//      jwtService.invalidateToken(accessToken);
//    }
//    refreshTokenRepository.deleteById(member.getMember().getId());
//
//    CookieUtils.clearAllRefreshCookies(res, true, sameSite());
//
//    res.setStatus(HttpServletResponse.SC_OK);
//  }
//
//
//  @Override
//  public Map<String, Object>  refresh(HttpServletRequest request, HttpServletResponse response) {
//// 1) 다중 RT 쿠키 중 "유효 + 저장소 매칭 + 최신(iat)" 선택
//
//    String refreshToken = resolveValidRefreshToken(request)
//        .orElseThrow(() -> new AuthException(ErrorCode.INVALID_TOKEN_ERROR));
//
//    UUID userId = UUID.fromString(jwtService.parseToken(refreshToken).getSubject());
//    RefreshToken stored = refreshTokenRepository.findById(userId)
//        .orElseThrow(() -> new AuthException(ErrorCode.INVALID_TOKEN_ERROR));
//    if (!stored.getToken().equals(refreshToken)) {
//      throw new AuthException(ErrorCode.INVALID_TOKEN_ERROR);
//    }
//
//    // 2) 새 Access
//    Member member = memberRepository.findById(userId)
//        .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));
//    String newAccessToken = jwtService.generateAccessToken(member, member.getRole().name());
//
//    // 3) ★ 회전(rotate) + 전량 삭제 후 단일 세팅
//    String newRefresh = jwtService.generateRefreshToken(userId);
//    stored.updateToken(newRefresh);
//    refreshTokenRepository.save(stored);
//
//    clearAllRefreshCookies(response, cookieSecure, sameSite());
//    setRefreshCookie(response, newRefresh, cookieSecure, sameSite());
//
//    return Map.of("accessToken", newAccessToken, "expiresIn", 900);
//  }
//
//
//  // ===== Helpers =====
//  private Optional<String> extractCookie(HttpServletRequest req, String name) {
//    Cookie[] cookies = req.getCookies();
//    if (cookies == null) return Optional.empty();
//    for (Cookie c : cookies) {
//      if (name.equals(c.getName())) {
//        return Optional.ofNullable(c.getValue());
//      }
//    }
//    return Optional.empty();
//  }
//
//  private Optional<String> extractAccessTokenFromHeader(HttpServletRequest req) {
//    String authHeader = req.getHeader("Authorization");
//    if (authHeader == null) return Optional.empty();
//    final String prefix = "Bearer ";
//    if (authHeader.startsWith(prefix) && authHeader.length() > prefix.length()) {
//      return Optional.of(authHeader.substring(prefix.length()));
//    }
//    return Optional.empty();
//  }
//
//  private Optional<String> resolveValidRefreshToken(HttpServletRequest request) {
//    Cookie[] cookies = request.getCookies();
//    if (cookies == null) return Optional.empty();
//
//    return Arrays.stream(cookies)
//        .filter(c -> REFRESH_COOKIE.equals(c.getName()))
//        .map(Cookie::getValue)
//        .filter(jwtService::validateToken)
//        .sorted((a, b) -> {
//          Date ia = jwtService.parseToken(a).getIssuedAt();
//          Date ib = jwtService.parseToken(b).getIssuedAt();
//          long la = ia != null ? ia.getTime() : 0L;
//          long lb = ib != null ? ib.getTime() : 0L;
//          return Long.compare(lb, la); // 최신 우선
//        })
//        .filter(t -> {
//          UUID uid = UUID.fromString(jwtService.parseToken(t).getSubject());
//          return refreshTokenRepository.findById(uid)
//              .map(RefreshToken::getToken)
//              .filter(stored -> stored.equals(t))
//              .isPresent();
//        })
//        .findFirst();
//  }
//}



/** 책임: 회원가입/로그아웃/리프레시 등 인증 유스케이스 오케스트레이션 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final MemberRepository memberRepository;
  private final MemberMapper memberMapper;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final TokenInvalidationService invalidationService;
  private final RefreshTokenRepository refreshTokenRepository;

  private static final String ACCESS_COOKIE = "ACCESS_TOKEN";
  private static final String REFRESH_COOKIE = "REFRESH_TOKEN";

  @Value("${app.cookies.secure:true}")
  private boolean cookieSecure;

  /** SameSite 정책 계산 */
  private String sameSite() { return cookieSecure ? "None" : "Lax"; }

  /** 회원가입 */
  @Override
  public MemberResponseDto register(HttpServletResponse res, RegisterRequestDTO dto) {
    if (!Objects.equals(dto.password(), dto.checkPassword()))
      throw new AuthException(ErrorCode.REGISTER_EXCEPTION);
    Member m = memberMapper.toEntity(dto);
    m.updatePassword(passwordEncoder.encode(dto.password()));
    m.updateStatus(MemberStatus.ACTIVE);
    m.updateRole(MemberRole.USER);
    return memberMapper.toResponseDto(memberRepository.save(m));
  }

  /** 로그아웃: 토큰 무효화 + 저장본 삭제 + 쿠키 정리 */
  @Override
  public void signOut(HttpServletRequest req, HttpServletResponse res, PrincipalMember pm) {
    extractAccessTokenFromHeader(req).ifPresent(t ->
        invalidationService.invalidate(t, jwtService.extractExpiration(t).toInstant()));
    extractCookie(req, REFRESH_COOKIE).ifPresent(t ->
        invalidationService.invalidate(t, jwtService.extractExpiration(t).toInstant()));
    refreshTokenRepository.deleteById(pm.getMember().getId());
    clearAllRefreshCookies(res, cookieSecure, sameSite());
    res.setStatus(HttpServletResponse.SC_OK);
  }

  /** 리프레시: 저장본 매칭 + 회전 + 단일 쿠키 재설정 */
  @Override
  public Map<String, Object> refresh(HttpServletRequest req, HttpServletResponse res) {
    String rt = resolveValidRefreshToken(req)
        .orElseThrow(() -> new AuthException(ErrorCode.INVALID_TOKEN_ERROR));
    jwtService.assertValid(rt);
    invalidationService.assertNotInvalid(rt);

    UUID uid = UUID.fromString(jwtService.parse(rt).getSubject());
    RefreshToken stored = refreshTokenRepository.findById(uid)
        .orElseThrow(() -> new AuthException(ErrorCode.INVALID_TOKEN_ERROR));
    if (!stored.getToken().equals(rt)) throw new AuthException(ErrorCode.INVALID_TOKEN_ERROR);

    Member member = memberRepository.findById(uid)
        .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));
    String newAccess = jwtService.generateAccessToken(member, member.getRole().name());
    String newRefresh = jwtService.generateRefreshToken(uid);
    stored.updateToken(newRefresh);
    refreshTokenRepository.save(stored);

    clearAllRefreshCookies(res, cookieSecure, sameSite());
    setRefreshCookie(res, newRefresh, cookieSecure, sameSite());
    return Map.of("accessToken", newAccess, "expiresIn", 900);
  }

  /** 쿠키 추출 */
  private Optional<String> extractCookie(HttpServletRequest req, String name) {
    Cookie[] cs = req.getCookies();
    if (cs == null) return Optional.empty();
    return Arrays.stream(cs).filter(c -> name.equals(c.getName()))
        .map(Cookie::getValue).findFirst();
  }

  /** Authorization 헤더에서 액세스 토큰 추출 */
  private Optional<String> extractAccessTokenFromHeader(HttpServletRequest req) {
    String h = req.getHeader("Authorization");
    String p = "Bearer ";
    if (h == null || !h.startsWith(p) || h.length() <= p.length()) return Optional.empty();
    return Optional.of(h.substring(p.length()));
  }

  /** 유효·저장본 매칭·최신(iat) RT 선택 */
  private Optional<String> resolveValidRefreshToken(HttpServletRequest req) {
    Cookie[] cs = req.getCookies();
    if (cs == null) return Optional.empty();
    Stream<String> rts = Arrays.stream(cs)
        .filter(c -> REFRESH_COOKIE.equals(c.getName()))
        .map(Cookie::getValue);
    return rts.filter(t -> { try { jwtService.assertValid(t); return true; }
        catch (Exception e) { return false; } })
        .sorted((a, b) -> {
          long la = Optional.ofNullable(jwtService.parse(a).getIssuedAt()).map(Date::getTime).orElse(0L);
          long lb = Optional.ofNullable(jwtService.parse(b).getIssuedAt()).map(Date::getTime).orElse(0L);
          return Long.compare(lb, la); // 최신 우선
        })
        .filter(t -> {
          UUID uid = UUID.fromString(jwtService.parse(t).getSubject());
          return refreshTokenRepository.findById(uid)
              .map(RefreshToken::getToken).filter(st -> st.equals(t)).isPresent();
        })
        .findFirst();
  }
}
