package com.example.blog.mapper;

import com.example.blog.domain.blog.dto.BlogResponseDto;
import com.example.blog.domain.blog.dto.BlogWithStat;
import com.example.blog.domain.blog.dto.CreateBlogRequestDto;
import com.example.blog.domain.blog.entity.Blog;
import com.example.blog.domain.member.entity.Member;
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

  @Mapping(target = "blogId", source = "id")
  @Mapping(target = "memberId", source = "member.id")
  @Mapping(target = "tags", expression = "java(blog.getTagNames())")
  BlogResponseDto toResponse(Blog blog);


  List<BlogResponseDto> toResponseList(List<Blog> blogs);

  @Mapping(target = "blogId", source = "blog.id")
  @Mapping(target = "memberId", source = "blog.member.id")
  @Mapping(target = "title", source = "blog.title")
  @Mapping(target = "tags", expression = "java(blogWithStat.blog().getTagNames())")
  @Mapping(target = "description", source = "blog.description")
  @Mapping(target = "visibility", source = "blog.visibility")
  @Mapping(target = "slug", source = "blog.slug")
  @Mapping(target = "views", source = "stat.viewCount")
  @Mapping(target = "posts", source = "stat.postCount")
  @Mapping(target = "followers", source = "stat.followerCount")
  @Mapping(target = "createdAt", source = "blog.createdAt")
  @Mapping(target = "updatedAt", source = "blog.updatedAt")
  BlogResponseDto toResponse(BlogWithStat blogWithStat);

  List<BlogResponseDto> toResponseListWithStat(List<BlogWithStat> blogs);


//  @AfterMapping
//  default void mapTags(Blog blog, @MappingTarget BlogResponseDto.BlogResponseDtoBuilder responseBuilder) {
//    List<String> tagNames = blog.getBlogTags().stream()
//        .map(blogTag -> blogTag.getTag().getName())
//        .toList();
//
//    responseBuilder.tags(tagNames);
//  }
}
