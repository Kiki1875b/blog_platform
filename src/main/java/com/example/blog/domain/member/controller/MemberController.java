package com.example.blog.domain.member.controller;



import com.example.blog.auth.user_details.CustomPrincipal;
import com.example.blog.common.aws.s3.S3Service;
import com.example.blog.domain.member.dto.MemberResponseDto;
import com.example.blog.domain.member.dto.UpdateMemberRequestDto;
import com.example.blog.domain.member.service.MemberService;
import com.example.blog.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

  private final MemberMapper memberMapper;
  private final MemberService memberService;
  @PostMapping
  public ResponseEntity<MemberResponseDto> updateMember(
      @ModelAttribute UpdateMemberRequestDto updateRequest,
      @AuthenticationPrincipal CustomPrincipal principal
  ){

    MemberResponseDto response = memberMapper.toResponseDto(memberService.updateMember(updateRequest, principal));
    return ResponseEntity.ok(response);
  }
}
