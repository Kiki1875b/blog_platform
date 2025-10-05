package com.example.blog.domain.post.repository;

import com.example.blog.domain.post.dto.PostPaginationRequest;
import com.example.blog.domain.post.dto.PostWithStat;
import com.example.blog.domain.post.entity.Post;
import java.util.List;
import java.util.UUID;

public interface PostRepositoryPort {
  Post findById(UUID uuid);
  Post findByIdJoinTag(UUID id);
  Post save(Post post);
  List<PostWithStat> findByBlogIdAndQuery(UUID id, PostPaginationRequest query);
}
