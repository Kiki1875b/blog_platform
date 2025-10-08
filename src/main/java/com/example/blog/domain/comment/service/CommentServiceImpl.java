package com.example.blog.domain.comment.service;

import com.example.blog.auth.user_details.CustomPrincipal;
import com.example.blog.common.exception.CommentException;
import com.example.blog.common.exception.ErrorCode;
import com.example.blog.domain.comment.dto.CreateCommentRequestDto;
import com.example.blog.domain.comment.entity.Comment;
import com.example.blog.domain.comment.mapper.CommentMapper;
import com.example.blog.domain.comment.repository.CommentRepositoryPort;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.service.MemberService;
import com.example.blog.domain.post.entity.Post;
import com.example.blog.domain.post.service.PostQueryService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepositoryPort commentRepositoryPort;
    private final PostQueryService postQueryService;
    private final MemberService memberService;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public Comment createComment(CustomPrincipal principal, UUID postId, CreateCommentRequestDto request) {
        Member member = memberService.findMemberProxy(principal);
        Post post = postQueryService.getPostById(postId);

        Comment parentComment = null;
        if (request.parentCommentId() != null) {
            parentComment = commentRepositoryPort.findById(request.parentCommentId())
                .orElseThrow(() -> new CommentException(ErrorCode.PARENT_COMMENT_NOT_FOUND));
        }

        Comment comment = commentMapper.toEntity(request, post, member, parentComment);

        return commentRepositoryPort.save(comment);
    }
}