package com.example.blog.domain.post.controller;

import com.example.blog.auth.user_details.CustomPrincipal;
import com.example.blog.domain.post.dto.CreatePostRequestDto;
import com.example.blog.domain.post.dto.PostResponseDto;
import com.example.blog.domain.post.facade.PostFacade;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
