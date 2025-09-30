package com.example.blog.common.pagenation;

import com.example.blog.domain.post.dto.PostResponseDto;
import java.util.List;
import java.util.UUID;

public record BlogPostPaginatedResponse(
    UUID blogId,
    String title,
    String description,
    List<String> tags,
    PaginatedResponse<PostResponseDto> paginatedResponse
) {
}
