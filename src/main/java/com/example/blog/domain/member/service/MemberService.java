package com.example.blog.domain.member.service;


import com.example.blog.auth.user_details.CustomPrincipal;
import com.example.blog.domain.member.dto.UpdateMemberRequestDto;
import com.example.blog.domain.member.entity.Member;
import java.util.UUID;

public interface MemberService {
  Member findMember(CustomPrincipal principal);
  Member updateMember(UpdateMemberRequestDto request, CustomPrincipal principal);
  Member findMemberById(UUID memberId);
}
