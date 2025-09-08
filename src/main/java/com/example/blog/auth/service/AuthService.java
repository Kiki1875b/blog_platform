package com.example.blog.auth.service;
import com.example.blog.auth.dto.RegisterRequestDTO;
import com.example.blog.auth.user_details.CustomPrincipal;
import com.example.blog.domain.member.dto.MemberResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

public interface AuthService {
  MemberResponseDto register(HttpServletResponse response, RegisterRequestDTO registerDto);
  void signOut(HttpServletRequest req, HttpServletResponse res, CustomPrincipal principal);
  Map<String, Object> refresh(HttpServletRequest request, HttpServletResponse response);
}
