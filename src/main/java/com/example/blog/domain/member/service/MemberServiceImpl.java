package com.example.blog.domain.member.service;

import com.example.blog.auth.user_details.CustomPrincipal;
import com.example.blog.common.exception.AuthException;
import com.example.blog.common.exception.ErrorCode;
import com.example.blog.common.exception.MemberException;
import com.example.blog.common.policy.interf.ProfileImageKeyPolicy;
import com.example.blog.domain.member.dto.UpdateMemberRequestDto;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.repository.MemberRepositoryPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

  private final MemberRepositoryPort memberPort;
  private final PasswordEncoder encoder;
  private final ProfileImageKeyPolicy profilePolicy;

  @Override
  public Member findMember(CustomPrincipal principal) {
    return memberPort.findById(principal.id()).orElseThrow(() -> new MemberException(ErrorCode.USER_NOT_FOUND));
  }

  @Override
  @Transactional
  public Member updateMember(UpdateMemberRequestDto request, CustomPrincipal principal) {

    Member foundMember = findOrThrowMember(principal.id());

    if(foundMember.getProvider() == null && !isPasswordBlank(request.password()) ){
      validatePassword(request.currentPassword(), foundMember.getPassword());
      foundMember.updatePassword(encoder.encode(request.password()));
    }

    foundMember.updateNickname(request.nickname());
    profilePolicy.validateOwnedKey(request.s3Key(), foundMember.getId());
    foundMember.updateProfileUrl(request.s3Key());

    return foundMember;
  }

  @Override
  public Member findMemberById(UUID memberId) {
    return memberPort.findById(memberId).orElseThrow(() -> new MemberException(ErrorCode.USER_NOT_FOUND));
  }

  private void validatePassword(String currentPassword, String originalPassword){
    if(!encoder.matches(currentPassword, originalPassword)){
      throw new AuthException(ErrorCode.PASSWORD_MATCH_ERROR);
    }
  }

  private Member findOrThrowMember(UUID memberId){
    return memberPort.findById(memberId)
        .orElseThrow(()-> new MemberException(ErrorCode.USER_NOT_FOUND));
  }

  private boolean isPasswordBlank(String password){
    return password == null || password.isEmpty();
  }
}
