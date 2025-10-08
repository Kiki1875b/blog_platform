package com.example.blog.domain.comment.repository;

import com.example.blog.domain.comment.entity.Comment;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    @Query("""
        SELECT c FROM comments c
        WHERE c.parent is null AND c.post.id = :postId
    """)
    List<Comment> findRootComments(UUID postId);
    @Query("SELECT c FROM comments c LEFT JOIN FETCH c.member LEFT JOIN FETCH c.parent WHERE c.post.id = :postId ORDER BY c.createdAt ASC")
    List<Comment> findByPostId(UUID postId);

    List<Comment> findByParentIdIn(Collection<UUID> ids);

}
