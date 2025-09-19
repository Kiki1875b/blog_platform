package com.example.blog.domain.post.repository;

import com.example.blog.domain.post.entity.Post;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, UUID> {

}
