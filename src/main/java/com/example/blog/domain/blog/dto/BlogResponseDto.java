package com.example.blog.domain.blog.dto;

import com.example.blog.domain.blog.entity.BlogVisibility;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record BlogResponseDto(
   UUID blogId,
   UUID memberId,
   String title,
   List<String> tags,
   String description,
   BlogVisibility visibility,
   String slug,
   Instant createdAt,
   Instant updatedAt
) {
}
