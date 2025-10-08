package com.example.blog.domain.comment.facade;

import com.example.blog.auth.user_details.CustomPrincipal;
import com.example.blog.domain.comment.dto.CommentResponseDto;
import com.example.blog.domain.comment.dto.CreateCommentRequestDto;
import com.example.blog.domain.comment.dto.PostCommentResponseDto;
import com.example.blog.domain.comment.entity.Comment;
import com.example.blog.domain.comment.mapper.CommentMapper;
import com.example.blog.domain.comment.service.CommentService;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CommentFacadeImpl implements CommentFacade {

    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public CommentResponseDto createComment(CustomPrincipal principal, UUID postId, CreateCommentRequestDto dto) {
        Comment comment = commentService.createComment(principal, postId, dto);
        return commentMapper.toResponse(comment);
    }

    @Override
    public List<PostCommentResponseDto> getPostComments(UUID postId) {
        List<Comment> roots = commentService.getRootCommentsByPostId(postId); // ORDER BY createdAt ASC
        List<Comment> children = commentService.getChildCommentsIn(roots);    // ORDER BY createdAt ASC

        Map<UUID, PostCommentResponseDto> dtoMap = new LinkedHashMap<>();

        for (Comment root : roots) {
            dtoMap.put(root.getId(), commentMapper.toPostCommentResponse(root));
        }

        for (Comment child : children) {
            PostCommentResponseDto parentDto = dtoMap.get(child.getParent().getId());
            parentDto.getChildComments().add(
                commentMapper.toPostCommentResponse(child)
            );
        }

        return new ArrayList<>(dtoMap.values());
    }
}
