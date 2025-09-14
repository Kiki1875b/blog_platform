package com.example.blog.domain.blog.dto;

import com.example.blog.domain.blog.entity.BlogVisibility;
import java.util.List;

public record UpdateBlogRequestDto (
    String title,
    List<String> tags,
    String description,
    BlogVisibility visibility
){

}
