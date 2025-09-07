package com.example.blog.domain.blog.respository;

import com.example.blog.domain.blog.entity.Blog;
import java.util.Optional;

public interface BlogRepositoryPort {
  Optional<Blog> findBySlug(String slug);
  Blog save(Blog blog);

}
