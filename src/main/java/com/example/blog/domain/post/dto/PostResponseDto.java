package com.example.blog.domain.post.dto;

import com.example.blog.domain.post.entity.PostState;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PostResponseDto(
    UUID postId,
    String title,
    String content,
    PostState status,
    List<String> tags,
    Instant createdAt
) {

}
