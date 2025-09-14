package com.example.blog.domain.post.service;

import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.post.dto.CreatePostRequestDto;
import com.example.blog.domain.post.entity.Post;

public interface PostCommandService {
  Post createPost(Member member, CreatePostRequestDto request);
}
