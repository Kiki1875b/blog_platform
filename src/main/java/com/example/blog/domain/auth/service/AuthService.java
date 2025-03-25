package com.example.blog.domain.auth.service;

import com.example.blog.domain.auth.dto.LoginDto;
import com.example.blog.domain.auth.dto.RegisterRequestDTO;
import com.example.blog.domain.member.MemberResponseDto;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
  MemberResponseDto register(HttpServletResponse response, RegisterRequestDTO registerDto);

  MemberResponseDto login(LoginDto loginDto);
}
