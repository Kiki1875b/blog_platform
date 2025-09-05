package com.example.blog.domain.member.service;

import com.example.blog.auth.service.PrincipalMember;
import com.example.blog.common.aws.s3.S3Service;
import com.example.blog.common.exception.AuthException;
import com.example.blog.common.exception.ErrorCode;
import com.example.blog.common.exception.MemberException;
import com.example.blog.common.policy.interf.ProfileImageKeyPolicy;
import com.example.blog.domain.member.dto.UpdateMemberRequestDto;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.repository.MemberRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder encoder;
  private final ProfileImageKeyPolicy profilePolicy;
  @Override
  @Transactional
  public Member updateMember(UpdateMemberRequestDto request, PrincipalMember member) {

    Member foundMember = findOrThrowMember(member.getMember().getId());

    if(foundMember.getProvider() == null){
      validatePassword(request.password(), foundMember.getPassword());
      foundMember.updatePassword(encoder.encode(request.password()));
    }

    foundMember.updateNickname(request.nickname());
    profilePolicy.validateOwnedKey(request.s3Key(), foundMember.getId());
    foundMember.updateProfileUrl(request.s3Key());

    return foundMember;
  }

  private void validatePassword(String currentPassword, String originalPassword){
    if(!encoder.matches(currentPassword, originalPassword)){
      throw new AuthException(ErrorCode.PASSWORD_MATCH_ERROR);
    }
  }

  private Member findOrThrowMember(UUID memberId){
    return memberRepository.findById(memberId)
        .orElseThrow(()-> new MemberException(ErrorCode.USER_NOT_FOUND));
  }
}
