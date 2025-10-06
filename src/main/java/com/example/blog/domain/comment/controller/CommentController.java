package com.example.blog.domain.comment.controller;

import com.example.blog.auth.user_details.CustomPrincipal;
import com.example.blog.domain.comment.dto.CommentResponseDto;
import com.example.blog.domain.comment.dto.CreateCommentRequestDto;
import com.example.blog.domain.comment.entity.Comment;
import com.example.blog.domain.comment.mapper.CommentMapper;
import com.example.blog.domain.comment.service.CommentService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponseDto> createComment(
        @AuthenticationPrincipal CustomPrincipal principal,
        @PathVariable UUID postId,
        @RequestBody @Valid CreateCommentRequestDto request
    ) {
        Comment comment = commentService.createComment(principal, postId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentMapper.toResponse(comment));
    }
}
