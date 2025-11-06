package com.example.blog.domain.comment.repository;

import com.example.blog.domain.comment.entity.Comment;
import java.util.List;
import java.util.UUID;

public interface CommentRepositoryPort {
    Comment save(Comment comment);

    Comment findById(UUID commentId);
    List<Comment> findRootByPostId(UUID postId);

    List<Comment> findChildCommentsOf(List<UUID> ids);

    void deleteById(UUID commentId);
}
