package com.example.blog.auth.controller;


import com.example.blog.auth.dto.RegisterRequestDTO;
import com.example.blog.auth.service.AuthService;
import com.example.blog.auth.user_details.CustomPrincipal;
import com.example.blog.domain.member.dto.MemberResponseDto;
import com.example.blog.domain.member.service.MemberService;
import com.example.blog.mapper.MemberMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
  private final MemberMapper memberMapper;
  private final MemberService memberService;

  @PostMapping("/register")
  public ResponseEntity<Void> register(HttpServletResponse response, @RequestBody RegisterRequestDTO register){
    MemberResponseDto memberDto = authService.register(response, register);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/signout")
  public ResponseEntity<Void> signOut(HttpServletRequest request, HttpServletResponse response, @AuthenticationPrincipal CustomPrincipal principal){
    authService.signOut(request, response, principal);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/me")
  public ResponseEntity<?> getMe(HttpServletRequest req, HttpServletResponse res, @AuthenticationPrincipal CustomPrincipal principal){

    if(principal == null){
      return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).build();
    }

    MemberResponseDto dto = memberMapper.toResponseDto(memberService.findMember(principal));
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

}
