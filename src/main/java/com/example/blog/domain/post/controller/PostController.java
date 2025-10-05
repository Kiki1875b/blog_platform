package com.example.blog.domain.post.controller;

import com.example.blog.auth.user_details.CustomPrincipal;
import com.example.blog.common.pagenation.BlogPostPaginatedResponse;
import com.example.blog.domain.post.dto.CreatePostRequestDto;
import com.example.blog.domain.post.dto.PostPaginationRequest;
import com.example.blog.domain.post.dto.PostResponseDto;
import com.example.blog.domain.post.facade.PostFacade;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.domain.post.dto.UpdatePostRequestDto;
import org.springframework.web.bind.annotation.PatchMapping;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {

  private final PostFacade postFacade;

  @PostMapping("/blogs/{blogId}/posts")
  public ResponseEntity<PostResponseDto> createBlogPost(@AuthenticationPrincipal CustomPrincipal principal, @RequestBody
  CreatePostRequestDto request, @PathVariable UUID blogId){

    return ResponseEntity.ok(postFacade.createPost(principal, request, blogId));
  }

  @GetMapping("/posts/{postId}")
  public ResponseEntity<PostResponseDto> getPost(@PathVariable UUID postId){
    return ResponseEntity.ok(postFacade.getSinglePostById(postId));
  }

  @GetMapping("/blogs/{blogId}/posts")
  public ResponseEntity<BlogPostPaginatedResponse> getBlogPosts(
      @PathVariable UUID blogId,
      @ModelAttribute PostPaginationRequest request
  ){
    BlogPostPaginatedResponse res = postFacade.getBlogPosts(blogId, request);
    return ResponseEntity.ok(res);
  }

  @PatchMapping("/posts/{postId}")
  public ResponseEntity<PostResponseDto> patchPost(
      @AuthenticationPrincipal CustomPrincipal principal,
      @PathVariable UUID postId,
      @RequestBody UpdatePostRequestDto request
  ) {
    return ResponseEntity.ok(postFacade.updatePost(principal, postId, request));
  }


}
