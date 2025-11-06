package com.example.blog.domain.comment.facade;

import com.example.blog.auth.user_details.CustomPrincipal;
import com.example.blog.domain.comment.dto.CommentResponseDto;
import com.example.blog.domain.comment.dto.CreateCommentRequestDto;
import com.example.blog.domain.comment.dto.PostCommentResponseDto;
import java.util.List;
import java.util.UUID;

public interface CommentFacade {
  CommentResponseDto createComment(CustomPrincipal principal, UUID postId, CreateCommentRequestDto dto);
  List<PostCommentResponseDto> getPostComments(UUID postId);

  CommentResponseDto updateComment(UUID commentId, UUID authorId, String content);

  void deleteComment(UUID commentId, UUID id);
}
