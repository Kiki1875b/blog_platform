package com.example.blog.domain.post.service;

import com.example.blog.domain.blog.entity.Blog;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.post.dto.CreatePostRequestDto;
import com.example.blog.domain.post.entity.Post;
import com.example.blog.domain.tag.entity.Tag;
import java.util.List;

public interface PostCommandService {
  Post createPost(Member member, Blog blog, List<Tag> tags, CreatePostRequestDto request);
}
