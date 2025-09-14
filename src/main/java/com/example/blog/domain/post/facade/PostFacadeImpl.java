package com.example.blog.domain.post.facade;

import com.example.blog.auth.user_details.CustomPrincipal;
import com.example.blog.domain.post.dto.CreatePostRequestDto;
import com.example.blog.domain.post.dto.PostResponseDto;
import com.example.blog.domain.post.service.PostCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostFacadeImpl implements PostFacade {

  private final PostCommandService postCommandService;
  @Override
  public PostResponseDto createPost(CustomPrincipal principal, CreatePostRequestDto request) {
    return null;
  }
}
