package com.example.blog.domain.post_stat.repository;

import com.example.blog.domain.post_stat.entity.PostStat;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostStatRepository extends JpaRepository<PostStat, UUID> {
  Optional<PostStat> findByPostId(UUID postId);
}
