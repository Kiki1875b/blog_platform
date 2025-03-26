package com.example.blog.auth.service;
import com.example.blog.auth.dto.RegisterRequestDTO;
import com.example.blog.domain.member.MemberResponseDto;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
  MemberResponseDto register(HttpServletResponse response, RegisterRequestDTO registerDto);

}
