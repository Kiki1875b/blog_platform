package com.example.blog.domain.auth.service;

import com.example.blog.auth.jwt.JwtUtil;
import com.example.blog.common.enumerated.MemberStatus;
import com.example.blog.common.exception.AuthException;
import com.example.blog.common.exception.ErrorCode;
import com.example.blog.domain.auth.dto.RegisterRequestDTO;
import com.example.blog.domain.member.MemberResponseDto;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.repository.MemberRepository;
import com.example.blog.mapper.MemberMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

  private final MemberRepository memberRepository;
  private final MemberMapper memberMapper;
  @Override
  public MemberResponseDto register(HttpServletResponse response, RegisterRequestDTO registerDto) {

    if(!Objects.equals(registerDto.password(), registerDto.checkPassword())){
      throw new AuthException(ErrorCode.REGISTER_EXCEPTION);
    }

    Member member = memberMapper.toEntity(registerDto);
    member.updateStatus(MemberStatus.ACTIVE);
    Member savedMember = memberRepository.save(member);

    return memberMapper.toResponseDto(savedMember);
  }
}
