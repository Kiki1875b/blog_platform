package com.example.blog.domain.post.repository;

import com.example.blog.domain.post.entity.Post;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, UUID> {


  @Query("""
    SELECT DISTINCT p
    FROM posts p
    LEFT JOIN FETCH p.postTags pt
    LEFT JOIN FETCH pt.tag
    WHERE p.id = :id
  """)
  Optional<Post> findByIdWithTags(UUID id);
}
