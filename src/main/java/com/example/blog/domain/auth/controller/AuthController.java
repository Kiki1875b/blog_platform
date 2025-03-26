package com.example.blog.domain.auth.controller;


import com.example.blog.auth.jwt.JwtUtil;
import com.example.blog.domain.auth.dto.LoginDto;
import com.example.blog.domain.auth.dto.RegisterRequestDTO;
import com.example.blog.domain.auth.service.AuthService;
import com.example.blog.domain.member.MemberResponseDto;
import com.example.blog.domain.refresh_token.RefreshToken;
import com.example.blog.domain.refresh_token.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;
  private final RefreshTokenRepository refreshTokenRepository;

  @PostMapping("/register")
  public ResponseEntity<MemberResponseDto> register(HttpServletResponse response, @RequestBody RegisterRequestDTO register){
    MemberResponseDto memberDto = authService.register(response, register);
    return generateTokens(response, memberDto);
  }


//  @PostMapping("/login")
//  public ResponseEntity<MemberResponseDto> login(HttpServletResponse response, @RequestBody
//      LoginDto loginDto){
//    MemberResponseDto memberDto = authService.login(loginDto);
//    return generateTokens(response, memberDto);
//  }


  private ResponseEntity<MemberResponseDto> generateTokens(HttpServletResponse response,
      MemberResponseDto memberDto) {
    String accessToken = JwtUtil.generateAccessToken(memberDto.id(), "USER"); //TODO : role 고민
    String refreshToken = JwtUtil.generateRefreshToken(memberDto.id());
    RefreshToken token = new RefreshToken(memberDto.id(), refreshToken);
    refreshTokenRepository.save(token);

    response.setHeader("Authorization", accessToken);
    response.setHeader("Refresh-Token", refreshToken);

    return ResponseEntity.ok(memberDto);
  }
}
