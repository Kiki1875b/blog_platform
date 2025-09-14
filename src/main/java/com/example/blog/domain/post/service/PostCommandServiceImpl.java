package com.example.blog.domain.post.service;

import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.post.dto.CreatePostRequestDto;
import com.example.blog.domain.post.entity.Post;
import org.springframework.stereotype.Service;

@Service
public class PostCommandServiceImpl implements PostCommandService{

  @Override
  public Post createPost(Member member, CreatePostRequestDto request) {
    return null;
  }
}
