package com.example.blog.domain.member;


import static org.mockito.BDDMockito.*;

import com.example.blog.auth.user_details.CustomPrincipal;
import com.example.blog.auth.user_details.CustomUserDetails;
import com.example.blog.common.enumerated.MemberStatus;
import com.example.blog.common.enumerated.Provider;
import com.example.blog.common.policy.interf.ProfileImageKeyPolicy;
import com.example.blog.domain.member.dto.UpdateMemberRequestDto;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.entity.MemberRole;
import com.example.blog.domain.member.repository.MemberRepository;
import com.example.blog.domain.member.service.MemberServiceImpl;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class MemberServiceImplTest {

  @Mock
  MemberRepository memberRepository;
  @Mock
  PasswordEncoder encoder;
  @Mock
  ProfileImageKeyPolicy policy;

  @InjectMocks
  MemberServiceImpl memberService;
  Member member;

  @BeforeEach
  void setUp(){
    member = new Member("email@email.com", "pwd", "nick", null, null, MemberStatus.ACTIVE, null, "name", MemberRole.USER);
    ReflectionTestUtils.setField(member, "id", UUID.randomUUID());
  }


  @Test
  @DisplayName("유효한 데이터로 사용자를 업데이트 할 수 있다")
  void 유효한_데이터로_사용자를_업데이트할_수_있다(){
    // given
    UpdateMemberRequestDto updateDto = new UpdateMemberRequestDto("update nick", "newPwd", "pwd", "s3Key");
    CustomPrincipal principal = new CustomPrincipal(member.getId(), member.getEmail(), member.getRole().toString(), member.getStatus().toString());
    given(memberRepository.findById(member.getId())).willReturn(
        Optional.ofNullable(member));
    given(encoder.matches(anyString(), anyString())).willReturn(true);
    given(encoder.encode(anyString())).willReturn(updateDto.password());
    willDoNothing().given(policy).validateOwnedKey(anyString(), any());

    // when
    Member member1 = memberService.updateMember(updateDto, principal  );

    // then
    Assertions.assertThat(member1.getNickname()).isEqualTo("update nick");
  }

  @Test
  @DisplayName("provider존재시 비밀번호 업데이트를 하지 않는다")
  void provider존재시_비밀번호_업데이트를_하지_않는다(){
    // given
    ReflectionTestUtils.setField(member, "provider", Provider.GOOGLE);
    UpdateMemberRequestDto updateDto = new UpdateMemberRequestDto("update nick", "newPwd", "pwd", "s3Key");
    CustomPrincipal principal = new CustomPrincipal(member.getId(), member.getEmail(), member.getRole().toString(), member.getStatus().toString());

    given(memberRepository.findById(member.getId())).willReturn(
        Optional.ofNullable(member));
    willDoNothing().given(policy).validateOwnedKey(anyString(), any());

    //when
    Member member1 = memberService.updateMember(updateDto, principal  );

    //then
    Assertions.assertThat(member1.getNickname()).isEqualTo("update nick");
    verifyNoInteractions(encoder);
  }
}
