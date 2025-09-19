package com.example.blog.domain.post.mapper;

import com.example.blog.domain.post.dto.CreatePostRequestDto;
import com.example.blog.domain.post.dto.PostResponseDto;
import com.example.blog.domain.post.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "blog", ignore = true)
  @Mapping(target = "member", ignore = true)
  @Mapping(target = "state", source = "status")
  @Mapping(target = "contentHtml", ignore = true)
  Post toEntity(CreatePostRequestDto dto);


  @Mapping(target = "postId", source = "id")
  @Mapping(target = "blogId", source = "blog.id")
  @Mapping(target = "authorId", source = "member.id")
  @Mapping(target = "status", source = "state")
  @Mapping(target = "tags", expression = "java(post.getTagNames())")
  PostResponseDto toResponse(Post post);
}
