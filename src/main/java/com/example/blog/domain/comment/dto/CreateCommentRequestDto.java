package com.example.blog.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record CreateCommentRequestDto(
    @NotBlank
    String content,
    UUID parentCommentId
) {}
