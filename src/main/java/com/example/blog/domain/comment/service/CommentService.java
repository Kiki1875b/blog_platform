package com.example.blog.domain.comment.service;

import com.example.blog.auth.user_details.CustomPrincipal;
import com.example.blog.domain.comment.dto.CreateCommentRequestDto;
import com.example.blog.domain.comment.entity.Comment;
import java.util.List;
import java.util.UUID;

public interface CommentService {
    Comment createComment(CustomPrincipal principal, UUID postId, CreateCommentRequestDto request);

    List<Comment> getRootCommentsByPostId(UUID postId);

    List<Comment> getChildCommentsIn(List<Comment> comments);
}
