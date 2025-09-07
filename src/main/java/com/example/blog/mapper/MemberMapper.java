package com.example.blog.mapper;

import com.example.blog.auth.dto.RegisterRequestDTO;
import com.example.blog.common.enumerated.Provider;
import com.example.blog.common.utils.S3UrlMapper;
import com.example.blog.domain.member.dto.MemberResponseDto;
import com.example.blog.domain.member.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {S3UrlMapper.class})
public interface MemberMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "provider", ignore = true)
  @Mapping(target = "providerId", ignore = true)
  @Mapping(target = "status", ignore = true)
  Member toEntity(RegisterRequestDTO dto);

  @Mapping(target = "role", constant = "USER")
  @Mapping(target = "status", constant = "ACTIVE")
  Member fromOAuthToMember(String name, String email, String providerId, Provider provider, String nickname);

  @Mapping(target = "profileUrl", source = "profileUrl", qualifiedByName = "toPublicUrl")
  MemberResponseDto toResponseDto(Member member);
}
