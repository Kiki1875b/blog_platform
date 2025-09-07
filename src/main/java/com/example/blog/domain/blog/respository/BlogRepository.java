package com.example.blog.domain.blog.respository;

import com.example.blog.domain.blog.entity.Blog;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Blog, UUID> {
  Optional<Blog> findBySlug(String slug);
}
