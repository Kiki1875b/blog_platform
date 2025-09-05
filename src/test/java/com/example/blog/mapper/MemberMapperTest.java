package com.example.blog.mapper;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

import com.example.blog.auth.dto.RegisterRequestDTO;
import com.example.blog.common.enumerated.MemberStatus;
import com.example.blog.common.enumerated.Provider;
import com.example.blog.common.utils.S3UrlMapper;
import com.example.blog.domain.member.dto.MemberResponseDto;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.entity.MemberRole;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.test.util.ReflectionTestUtils;


public class MemberMapperTest {

  MemberMapper memberMapper;

  @BeforeEach
  void setUp(){
    memberMapper = new MemberMapperImpl();
  }

  @Test
  @DisplayName("회원가입 DTO를 mapping 할수 있다")
  void 회원가입_DTO를_매핑할_수_있다(){
    // given
    RegisterRequestDTO dto = new RegisterRequestDTO("email@email.com", "pwd", "pwd", "nick", "name");

    // when
    Member member = memberMapper.toEntity(dto);

    // then
    assertThat(member.getEmail()).isEqualTo(dto.email());
    assertThat(member.getNickname()).isEqualTo("nick");
  }

  @Test
  @DisplayName("OAuth정보를 매핑할 수 있다")
  void OAuth_정보를_매핑할_수_있다(){
    // given
    String name = "name";
    String email = "email@email.com";
    String providerId = UUID.randomUUID().toString();
    Provider provider = Provider.GOOGLE;
    String nickname = "nick";

    // when
    Member member = memberMapper.fromOAuthToMember(name, email, providerId, provider, nickname);

    // then
    assertThat(member.getName()).isEqualTo(name);
    assertThat(member.getEmail()).isEqualTo(email);
  }

  @Test
  @DisplayName("Member를 MemberResponseDto로 매핑할 수 있다")
  void Member를_MemberResponseDto_로_매핑할_수_있다(){
    // given
    String name = "name";
    String email = "email@email.com";
    String providerId = UUID.randomUUID().toString();
    Provider provider = Provider.GOOGLE;
    String nickname = "nick";
    String url = "https://test-url.com";
    Member member = new Member(email, "pwd", nickname, provider, providerId, MemberStatus.ACTIVE, null, name, MemberRole.USER );
    ReflectionTestUtils.setField(member, "id", UUID.randomUUID());
    S3UrlMapper s3UrlMapper = mock(S3UrlMapper.class);
    ReflectionTestUtils.setField(memberMapper, "s3UrlMapper", s3UrlMapper);
    BDDMockito.given(s3UrlMapper.toPublicUrl(any())).willReturn(url );

    // when
    MemberResponseDto responseDto = memberMapper.toResponseDto(member);

    // then
    assertThat(responseDto.email()).isEqualTo(email);
    assertThat(responseDto.name()).isEqualTo(name);
    assertThat(responseDto.profileUrl()).isEqualTo(url);

  }

}
