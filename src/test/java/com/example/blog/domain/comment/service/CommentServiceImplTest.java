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
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.assertj.core.api.Assertions.assertThat;


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
        when(commentRepositoryPort.findById(parentCommentId)).thenReturn(parentComment);
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
        when(commentRepositoryPort.findById(parentCommentId)).thenThrow(new CommentException(ErrorCode.PARENT_COMMENT_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> commentService.createComment(principal, postId, request))
                .isInstanceOf(CommentException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PARENT_COMMENT_NOT_FOUND);
    }

    @Test
    @DisplayName("댓글 수정 - 성공")
    void updateComment_success() {
        // given
        UUID commentId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        String newContent = "updated content";

        Member author = mock(Member.class);
        when(author.getId()).thenReturn(authorId);

        Comment comment = mock(Comment.class);
        when(comment.getMember()).thenReturn(author);

        when(commentRepositoryPort.findById(commentId)).thenReturn(comment);

        // when
        Comment updatedComment = commentService.updateComment(commentId, authorId, newContent);

        // then
        assertAll(
            () -> verify(comment).updateContent(newContent),
            () -> assertThat(updatedComment).isEqualTo(comment)
        );
    }

    @Test
    @DisplayName("댓글 수정 - 실패 (댓글을 찾을 수 없음)")
    void updateComment_fail_commentNotFound() {
        // given
        UUID commentId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        String newContent = "updated content";

        when(commentRepositoryPort.findById(commentId)).thenThrow(new CommentException(ErrorCode.COMMENT_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> commentService.updateComment(commentId, authorId, newContent))
            .isInstanceOf(CommentException.class)
            .hasMessage("댓글을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("댓글 수정 - 실패 (작성자 검증 실패)")
    void updateComment_fail_authorValidationFailed() {
        // given
        UUID commentId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        String newContent = "updated content";

        Member author = mock(Member.class);
        when(author.getId()).thenReturn(authorId);

        Comment comment = mock(Comment.class);
        when(comment.getMember()).thenReturn(author);

        when(commentRepositoryPort.findById(commentId)).thenReturn(comment);

        // when & then
        assertThatThrownBy(() -> commentService.updateComment(commentId, otherUserId, newContent))
            .isInstanceOf(CommentException.class)
            .hasMessage("작성자만 댓글을 수정할 수 있습니다.");
    }

    @Test
    @DisplayName("댓글 삭제 - 성공")
    void softDeleteComment_success() {
        // given
        UUID commentId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        Member author = mock(Member.class);
        when(author.getId()).thenReturn(authorId);

        Comment comment = mock(Comment.class);
        when(comment.getMember()).thenReturn(author);

        when(commentRepositoryPort.findById(commentId)).thenReturn(comment);

        // when
        commentService.softDeleteComment(commentId, authorId);

        // then
        verify(comment).softDelete();
    }

    @Test
    @DisplayName("댓글 삭제 - 실패 (댓글을 찾을 수 없음)")
    void softDeleteComment_fail_commentNotFound() {
        // given
        UUID commentId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        when(commentRepositoryPort.findById(commentId)).thenThrow(new CommentException(ErrorCode.COMMENT_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> commentService.softDeleteComment(commentId, authorId))
            .isInstanceOf(CommentException.class)
            .hasMessage("댓글을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("댓글 삭제 - 실패 (작성자 검증 실패)")
    void softDeleteComment_fail_authorValidationFailed() {
        // given
        UUID commentId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();

        Member author = mock(Member.class);
        when(author.getId()).thenReturn(authorId);

        Comment comment = mock(Comment.class);
        when(comment.getMember()).thenReturn(author);

        when(commentRepositoryPort.findById(commentId)).thenReturn(comment);

        // when & then
        assertThatThrownBy(() -> commentService.softDeleteComment(commentId, otherUserId))
            .isInstanceOf(CommentException.class)
            .hasMessage("작성자만 댓글을 수정할 수 있습니다.");
    }
}
