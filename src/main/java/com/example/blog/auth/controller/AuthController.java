package com.example.blog.auth.controller;


import com.example.blog.auth.dto.RegisterRequestDTO;
import com.example.blog.auth.service.AuthService;
import com.example.blog.auth.service.PrincipalMember;
import com.example.blog.domain.member.MemberResponseDto;
import com.example.blog.domain.refresh_token.RefreshToken;
import com.example.blog.domain.refresh_token.RefreshTokenRepository;
import com.example.blog.mapper.MemberMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
  private final MemberMapper memberMapper;

  @PostMapping("/register")
  public ResponseEntity<Void> register(HttpServletResponse response, @RequestBody RegisterRequestDTO register){
    MemberResponseDto memberDto = authService.register(response, register);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/signout")
  public ResponseEntity<Void> signOut(HttpServletRequest request, HttpServletResponse response){
    authService.signOut(request, response);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/me")
  public ResponseEntity<MemberResponseDto> getMe(HttpServletRequest req, HttpServletResponse res, @AuthenticationPrincipal PrincipalMember
      authentication){

    if(authentication == null){
      return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).build();
    }
    MemberResponseDto dto = memberMapper.toResponseDto(((PrincipalMember) authentication).getMember());

    System.out.println(dto.id());
    return ResponseEntity.ok(dto);
  }

  @PostMapping("/refresh")
  public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
    try {
      Map<String, Object> tokens = authService.refresh(request, response);
      return ResponseEntity.ok(tokens);
    } catch (Exception e) {
      return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).build();
    }
  }

//  private ResponseEntity<MemberResponseDto> generateTokens(HttpServletResponse response,
//      MemberResponseDto memberDto) {
//    String accessToken = JwtUtil.generateAccessToken(memberDto.id(), memberDto.role().name()); //TODO : role 고민
//    String refreshToken = JwtUtil.generateRefreshToken(memberDto.id());
//    RefreshToken token = new RefreshToken(memberDto.id(), refreshToken);
//    refreshTokenRepository.save(token);
//
//    response.setHeader("Authorization", accessToken);
//    response.setHeader("Refresh-Token", refreshToken);
//
//    return ResponseEntity.ok(memberDto);
//  }
}
