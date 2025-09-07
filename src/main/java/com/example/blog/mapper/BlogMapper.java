package com.example.blog.mapper;

import com.example.blog.domain.blog.dto.BlogResponseDto;
import com.example.blog.domain.blog.dto.CreateBlogRequestDto;
import com.example.blog.domain.blog.entity.Blog;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.tag.entity.Tag;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BlogMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "member", source = "member")
  @Mapping(target = "blogTags", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Blog toEntity(CreateBlogRequestDto blog, Member member);

  @Mapping(target = "blogId", source = "blog.id")
  @Mapping(target = "memberId", source = "blog.member.id")
  @Mapping(target = "tags", source = "tags")
  BlogResponseDto toResponse(Blog blog, List<String> tags);
}
