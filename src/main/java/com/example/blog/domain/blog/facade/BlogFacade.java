package com.example.blog.domain.blog.facade;

import com.example.blog.auth.user_details.CustomPrincipal;
import com.example.blog.common.pagenation.PaginatedResponse;
import com.example.blog.domain.blog.dto.BlogPaginationRequest;
import com.example.blog.domain.blog.dto.BlogResponseDto;
import com.example.blog.domain.blog.dto.CreateBlogRequestDto;
import com.example.blog.domain.blog.dto.UpdateBlogRequestDto;
import java.util.UUID;

public interface BlogFacade {
  BlogResponseDto createBlog(CreateBlogRequestDto request, CustomPrincipal principal);
  PaginatedResponse<BlogResponseDto> getMemberBlogs(UUID memberId, BlogPaginationRequest request);
  BlogResponseDto updateBlog(UUID blogId, UpdateBlogRequestDto request, CustomPrincipal principal);
}
