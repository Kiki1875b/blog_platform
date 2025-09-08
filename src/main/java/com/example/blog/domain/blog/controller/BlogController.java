package com.example.blog.domain.blog.controller;



import com.example.blog.auth.user_details.CustomPrincipal;
import com.example.blog.domain.blog.dto.BlogResponseDto;
import com.example.blog.domain.blog.dto.CreateBlogRequestDto;
import com.example.blog.domain.blog.service.BlogService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
public class BlogController {


  private final BlogService blogService;

  @PostMapping
  public ResponseEntity<BlogResponseDto> createBlog(@RequestBody CreateBlogRequestDto blogRequest, @AuthenticationPrincipal
      CustomPrincipal principal){

    BlogResponseDto responseDto = blogService.createBlog(blogRequest, principal);

    return ResponseEntity.ok(responseDto);
  }
}
