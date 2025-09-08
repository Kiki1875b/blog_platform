package com.example.blog.domain.blog.dto;

import com.example.blog.domain.blog.entity.BlogVisibility;
import java.util.List;

public record CreateBlogRequestDto (
  String title,
  List<String> tags,
  String description,
  String slug,
  BlogVisibility visibility
){
}
