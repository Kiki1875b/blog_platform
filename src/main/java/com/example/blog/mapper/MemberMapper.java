package com.example.blog.mapper;

import com.example.blog.auth.dto.RegisterRequestDTO;
import com.example.blog.domain.member.MemberResponseDto;
import com.example.blog.domain.member.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "provider", ignore = true)
  @Mapping(target = "providerId", ignore = true)
  @Mapping(target = "status", ignore = true)
  Member toEntity(RegisterRequestDTO dto);

  MemberResponseDto toResponseDto(Member member);
}
