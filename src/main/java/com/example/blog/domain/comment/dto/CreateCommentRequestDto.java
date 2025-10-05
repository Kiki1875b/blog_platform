package com.example.blog.domain.comment.dto;

import java.util.UUID;

public record CreateCommentRequestDto(
    String content,
    UUID parentCommentId
) {}
