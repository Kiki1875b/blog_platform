package com.example.blog.domain.refresh_token.controller;


import com.example.blog.auth.jwt.JwtService;
import com.example.blog.domain.refresh_token.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class RefreshTokenController {
  private final JwtService jwtService;
  private final RefreshTokenRepository refreshTokenRepository;

//  @PostMapping("/api/token/refresh")
//  public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenDTO request) {
//    try {
//      String refreshToken = request.refreshToken();
//      Claims claims = jwtService.parseToken(refreshToken);
//      Long userId = Long.parseLong(claims.getSubject());
//
//      RefreshToken savedToken = refreshTokenRepository.findById(userId).orElseThrow(
//          // TODO : 예외 처리
//      );
//
//      if (!savedToken.getToken().equalsIgnoreCase(request.refreshToken())) {
//        throw new IllegalArgumentException(); // TODO : 예외 처리
//      }
//
//      String newAccessToken = jwtService.generateAccessToken(userId, "ROLE_USER");
//      String newRefreshToken = jwtService.generateRefreshToken(userId);
//      savedToken.updateToken(newRefreshToken);
//      refreshTokenRepository.save(savedToken);
//
//      RefreshTokenDTO refreshTokenDTO = new RefreshTokenDTO(newRefreshToken);
//
//      return ResponseEntity.ok()
//          .header("Authorization", "Bearer " + newAccessToken)
//          .body(refreshTokenDTO);
//    } catch (Exception e) {
//      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//          .body("유효하지 않은 Refresh Token 입니다.");
//    }
//  }
}
