package com.example.blog.domain.comment.dto;

import java.time.Instant;
import java.util.UUID;

public record CommentResponseDto(
    UUID commentId,
    String content,
    UUID authorId,
    String authorNickname,
    Instant createdAt,
    UUID parentCommentId
) {}
