package com.example.blog.domain.comment.repository;

import com.example.blog.common.exception.CommentException;
import com.example.blog.common.exception.ErrorCode;
import com.example.blog.domain.comment.entity.Comment;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentJpaAdapter implements CommentRepositoryPort {
    private final CommentRepository commentRepository;

    @Override
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }


    @Override
    public Comment findById(UUID commentId) {
        return commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND));
    }

    @Override
    public List<Comment> findRootByPostId(UUID postId) {
        return commentRepository.findRootComments(postId);
    }

    @Override
    public List<Comment> findChildCommentsOf(List<UUID> ids) {
        return commentRepository.findByParentIdIn(ids);
    }

    @Override
    public void deleteById(UUID commentId) {
        commentRepository.deleteById(commentId);
    }
}
