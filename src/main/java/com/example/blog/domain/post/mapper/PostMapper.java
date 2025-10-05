package com.example.blog.domain.post.mapper;

import com.example.blog.domain.post.dto.CreatePostRequestDto;
import com.example.blog.domain.post.dto.PostResponseDto;
import com.example.blog.domain.post.dto.PostWithStat;
import com.example.blog.domain.post.entity.Post;
import com.example.blog.domain.post_stat.entity.PostStat;
import java.util.List;
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
  @Mapping(target = "views", expression = "java(0L)")
  @Mapping(target = "likes", expression = "java(0L)")
  @Mapping(target = "title", source = "title")
  @Mapping(target = "content", source = "content")
  @Mapping(target = "contentHtml", source = "contentHtml")
  @Mapping(target = "createdAt", source = "createdAt")
  PostResponseDto toResponse(Post post);

  @Mapping(target = "postId", source = "post.id")
  @Mapping(target = "blogId", source = "post.blog.id")
  @Mapping(target = "authorId", source = "post.member.id")
  @Mapping(target = "status", source = "post.state")
  @Mapping(target = "tags", expression = "java(post.getTagNames())")
  @Mapping(target = "views", source = "stat.viewCount")
  @Mapping(target = "likes", source = "stat.likeCount")
  @Mapping(target = "createdAt", source = "post.createdAt")
  @Mapping(target = "title", source = "post.title")
  @Mapping(target = "content", source = "post.content")
  @Mapping(target = "contentHtml", source = "post.contentHtml")
  PostResponseDto toResponse(Post post, PostStat stat);



  @Mapping(target = "postId", source = "post.id")
  @Mapping(target = "blogId", source = "post.blog.id")
  @Mapping(target = "authorId", source = "post.member.id")
  @Mapping(target = "title", source = "post.title")
  @Mapping(target = "content", source = "post.content")
  @Mapping(target = "contentHtml", source = "post.contentHtml")
  @Mapping(target = "status", source = "post.state")
  @Mapping(target = "tags", expression = "java(postWithStat.post().getTagNames())")
  @Mapping(target = "createdAt", source = "post.createdAt")
  @Mapping(target = "views", source = "stat.viewCount")
  @Mapping(target = "likes", source = "stat.likeCount")
  PostResponseDto toPostResponse(PostWithStat postWithStat);

  List<PostResponseDto> toPostListResponse(List<PostWithStat> stats);
}