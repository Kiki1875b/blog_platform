package com.example.blog.domain.comment.service;

import com.example.blog.auth.user_details.CustomPrincipal;
import com.example.blog.domain.comment.dto.CreateCommentRequestDto;
import com.example.blog.domain.comment.entity.Comment;
import com.example.blog.domain.comment.mapper.CommentMapper;
import com.example.blog.domain.comment.repository.CommentRepositoryPort;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.service.MemberService;
import com.example.blog.domain.post.entity.Post;
import com.example.blog.common.exception.CommentException;
import com.example.blog.domain.post.service.PostQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import com.example.blog.common.exception.PostException;
import com.example.blog.common.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepositoryPort commentRepositoryPort;

    @Mock
    private PostQueryService postQueryService;

    @Mock
    private MemberService memberService;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    @DisplayName("댓글 생성 - 루트 댓글")
    void createComment_root() {
        // given
        CustomPrincipal principal = new CustomPrincipal(UUID.randomUUID(), "test@test.com", "USER", "ACTIVE");
        UUID postId = UUID.randomUUID();
        CreateCommentRequestDto request = new CreateCommentRequestDto("test content", null);

        Member member = mock(Member.class);
        Post post = mock(Post.class);
        Comment comment = mock(Comment.class);

        when(memberService.findMemberProxy(principal)).thenReturn(member);
        when(postQueryService.getPostById(postId)).thenReturn(post);
        when(commentMapper.toEntity(request, post, member, null)).thenReturn(comment);

        // when
        commentService.createComment(principal, postId, request);

        // then
        verify(commentRepositoryPort).save(comment);
    }

    @Test
    @DisplayName("댓글 생성 - 대댓글")
    void createComment_nested() {
        // given
        CustomPrincipal principal = new CustomPrincipal(UUID.randomUUID(), "test@test.com", "USER", "ACTIVE");
        UUID postId = UUID.randomUUID();
        UUID parentCommentId = UUID.randomUUID();
        CreateCommentRequestDto request = new CreateCommentRequestDto("test content", parentCommentId);

        Member member = mock(Member.class);
        Post post = mock(Post.class);
        Comment parentComment = mock(Comment.class);
        Comment comment = mock(Comment.class);

        when(memberService.findMemberProxy(principal)).thenReturn(member);
        when(postQueryService.getPostById(postId)).thenReturn(post);
        when(commentRepositoryPort.findById(parentCommentId)).thenReturn(java.util.Optional.of(parentComment));
        when(commentMapper.toEntity(request, post, member, parentComment)).thenReturn(comment);

        // when
        commentService.createComment(principal, postId, request);

        // then
        verify(commentRepositoryPort).save(comment);
    }

    @Test
    @DisplayName("존재하지 않는 게시물 ID로 댓글 생성 시 PostException 발생")
    void createComment_invalidPostId_throwsPostException() {
        // given
        CustomPrincipal principal = new CustomPrincipal(UUID.randomUUID(), "test@test.com", "USER", "ACTIVE");
        UUID postId = UUID.randomUUID();
        CreateCommentRequestDto request = new CreateCommentRequestDto("test content", null);

        when(memberService.findMemberProxy(principal)).thenReturn(mock(Member.class));
        when(postQueryService.getPostById(postId)).thenThrow(new PostException(ErrorCode.POST_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> commentService.createComment(principal, postId, request))
                .isInstanceOf(PostException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POST_NOT_FOUND);
    }

    @Test
    @DisplayName("존재하지 않는 부모 댓글 ID로 대댓글 생성 시 CommentException 발생")
    void createComment_invalidParentCommentId_throwsCommentException() {
        // given
        CustomPrincipal principal = new CustomPrincipal(UUID.randomUUID(), "test@test.com", "USER", "ACTIVE");
        UUID postId = UUID.randomUUID();
        UUID parentCommentId = UUID.randomUUID();
        CreateCommentRequestDto request = new CreateCommentRequestDto("test content", parentCommentId);

        when(memberService.findMemberProxy(principal)).thenReturn(mock(Member.class));
        when(postQueryService.getPostById(postId)).thenReturn(mock(Post.class));
        when(commentRepositoryPort.findById(parentCommentId)).thenReturn(java.util.Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.createComment(principal, postId, request))
                .isInstanceOf(CommentException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PARENT_COMMENT_NOT_FOUND);
    }
}
