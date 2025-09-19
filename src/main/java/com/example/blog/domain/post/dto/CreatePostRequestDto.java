package com.example.blog.domain.post.dto;

import com.example.blog.domain.post.entity.PostState;
import java.util.List;

public record CreatePostRequestDto(
    String title,
    String content,
    PostState status,
    List<String> tags
) {

}
