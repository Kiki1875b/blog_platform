package com.example.blog.domain.comment.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentResponseDto(
    UUID commentId,
    String content,
    UUID authorId,
    String authorNickname,
    LocalDateTime createdAt,
    UUID parentCommentId
) {}
