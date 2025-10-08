package com.example.blog.domain.comment.repository;

import com.example.blog.domain.comment.entity.Comment;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepositoryPort {
    Comment save(Comment comment);
    Optional<Comment> findById(UUID commentId);
}
