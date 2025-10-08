package com.example.blog.domain.comment.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class PostCommentResponseDto {
    private UUID commentId;
    private String content;
    private UUID authorId;
    private String authorNickname;
    private Instant createdAt;
    private UUID parentCommentId;
    private List<PostCommentResponseDto> childComments = new ArrayList<>();

    public PostCommentResponseDto(UUID commentId, String content, UUID authorId,
        String authorNickname,
        Instant createdAt, UUID parentCommentId, List<PostCommentResponseDto> childComments) {
        this.commentId = commentId;
        this.content = content;
        this.authorId = authorId;
        this.authorNickname = authorNickname;
        this.createdAt = createdAt;
        this.parentCommentId = parentCommentId;
        this.childComments = childComments;
    }
}
