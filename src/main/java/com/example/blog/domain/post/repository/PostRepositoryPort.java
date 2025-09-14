package com.example.blog.domain.post.repository;

import com.example.blog.domain.post.entity.Post;
import java.util.UUID;

public interface PostRepositoryPort {
  Post findById(UUID uuid);
  Post save(Post post);
}
