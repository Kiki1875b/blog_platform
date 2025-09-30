package com.example.blog.domain.post.facade;

import com.example.blog.auth.user_details.CustomPrincipal;
import com.example.blog.common.pagenation.BlogPostPaginatedResponse;
import com.example.blog.domain.post.dto.CreatePostRequestDto;
import com.example.blog.domain.post.dto.PostPaginationRequest;
import com.example.blog.domain.post.dto.PostResponseDto;
import java.util.UUID;

public interface PostFacade {
  PostResponseDto createPost(CustomPrincipal principal, CreatePostRequestDto request, UUID blogId);
  PostResponseDto getSinglePostById(UUID blogId);

  BlogPostPaginatedResponse getBlogPosts(UUID blogId, PostPaginationRequest request);
}
