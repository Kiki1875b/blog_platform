package com.example.blog.domain.post.service;

import com.example.blog.domain.blog.entity.Blog;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.post.dto.CreatePostRequestDto;
import com.example.blog.domain.post.dto.UpdatePostRequestDto;
import com.example.blog.domain.post.entity.Post;
import com.example.blog.domain.tag.entity.Tag;
import java.util.List;
import java.util.UUID;

public interface PostCommandService {
  Post createPost(Member member, Blog blog, List<Tag> tags, CreatePostRequestDto request);

  void updatePost(UUID postId, Member member, List<Tag> tags, UpdatePostRequestDto request);
}
