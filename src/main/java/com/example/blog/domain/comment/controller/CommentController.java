package com.example.blog.domain.comment.controller;

import com.example.blog.auth.user_details.CustomPrincipal;
import com.example.blog.domain.comment.dto.CommentResponseDto;
import com.example.blog.domain.comment.dto.CreateCommentRequestDto;
import com.example.blog.domain.comment.dto.PostCommentResponseDto;
import com.example.blog.domain.comment.facade.CommentFacade;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentFacade commentFacade;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponseDto> createComment(
        @AuthenticationPrincipal CustomPrincipal principal,
        @PathVariable UUID postId,
        @RequestBody @Valid CreateCommentRequestDto request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentFacade.createComment(principal, postId, request));
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<PostCommentResponseDto>> getPostComments(@PathVariable UUID postId) {
        return ResponseEntity.ok(commentFacade.getPostComments(postId));
    }
}
