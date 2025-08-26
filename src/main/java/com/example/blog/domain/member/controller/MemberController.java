package com.example.blog.domain.member.controller;


import com.example.blog.auth.service.PrincipalMember;
import com.example.blog.domain.member.dto.MemberResponseDto;
import com.example.blog.domain.member.dto.UpdateMemberRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MemberResponseDto> updateMember(
      @ModelAttribute UpdateMemberRequestDto updateRequest,
      @AuthenticationPrincipal PrincipalMember principalMember
  ){


    UpdateMemberRequestDto updateMemberRequestDto = updateRequest;

    return ResponseEntity.ok(new MemberResponseDto(null ,null, null, null, null, null, null, null, null, null));
  }
}
