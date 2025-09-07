package com.example.blog.domain.blog.service;

import com.example.blog.auth.user_details.CustomPrincipal;
import com.example.blog.domain.blog.dto.BlogResponseDto;
import com.example.blog.domain.blog.dto.CreateBlogRequestDto;
import com.example.blog.domain.blog.entity.Blog;

public interface BlogService {
  BlogResponseDto createBlog(CreateBlogRequestDto request, CustomPrincipal principal);
}
