package com.example.blog.domain.member.service;


import com.example.blog.auth.service.PrincipalMember;
import com.example.blog.domain.member.dto.UpdateMemberRequestDto;
import com.example.blog.domain.member.entity.Member;

public interface MemberService {
  Member updateMember(UpdateMemberRequestDto request, PrincipalMember member);
}
