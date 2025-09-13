package com.example.blog.domain.blog.service;

import com.example.blog.auth.user_details.CustomPrincipal;
import com.example.blog.common.pagenation.PaginatedResponse;
import com.example.blog.domain.blog.dto.BlogPaginationRequest;
import com.example.blog.domain.blog.dto.BlogResponseDto;
import com.example.blog.domain.blog.dto.CreateBlogRequestDto;
import com.example.blog.domain.blog.dto.UpdateBlogRequestDto;
import com.example.blog.domain.blog.entity.Blog;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.tag.entity.Tag;
import java.util.List;
import java.util.UUID;

public interface BlogService {

  Blog createBlog(CreateBlogRequestDto request, CustomPrincipal principal);
  Blog updateBlog(UUID blogId, UpdateBlogRequestDto request, Member member, List<Tag> tags);
  void addTags(List<Tag> tags, Blog blog);
  Blog saveBlog(Blog blog);
  PaginatedResponse<BlogResponseDto> getMemberBlogs(UUID memberId, BlogPaginationRequest request);
}
