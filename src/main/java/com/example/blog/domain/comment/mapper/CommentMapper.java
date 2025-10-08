package com.example.blog.domain.comment.mapper;

import com.example.blog.domain.comment.dto.CommentResponseDto;
import com.example.blog.domain.comment.dto.CreateCommentRequestDto;
import com.example.blog.domain.comment.dto.PostCommentResponseDto;
import com.example.blog.domain.comment.entity.Comment;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.post.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", constant = "false")
    @Mapping(target = "content", source = "dto.content")
    @Mapping(target = "post", source = "post")
    @Mapping(target = "member", source = "member")
    @Mapping(target = "parent", source = "parent")
    Comment toEntity(CreateCommentRequestDto dto, Post post, Member member, Comment parent);

    @Mapping(target = "commentId", source = "comment.id")
    @Mapping(target = "authorId", source = "comment.member.id")
    @Mapping(target = "authorNickname", source = "comment.member.nickname")
    @Mapping(target = "parentCommentId", source = "comment.parent.id")
    CommentResponseDto toResponse(Comment comment);

    @Mapping(target = "commentId", source = "comment.id")
    @Mapping(target = "authorId", source = "comment.member.id")
    @Mapping(target = "authorNickname", source = "comment.member.nickname")
    @Mapping(target = "parentCommentId", source = "comment.parent.id")
    @Mapping(target = "childComments", expression = "java(new java.util.ArrayList<>())")
    PostCommentResponseDto toPostCommentResponse(Comment comment);
}
