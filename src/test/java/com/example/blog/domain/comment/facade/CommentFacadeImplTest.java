package com.example.blog.domain.comment.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.then;

import com.example.blog.auth.user_details.CustomPrincipal;
import com.example.blog.domain.comment.dto.CommentResponseDto;
import com.example.blog.domain.comment.dto.CreateCommentRequestDto;
import com.example.blog.domain.comment.dto.PostCommentResponseDto;
import com.example.blog.domain.comment.entity.Comment;
import com.example.blog.domain.comment.mapper.CommentMapper;
import com.example.blog.domain.comment.service.CommentService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentFacadeImplTest {

  @Mock
  private CommentService commentService;
  @Mock
  private CommentMapper commentMapper;

  @InjectMocks
  private CommentFacadeImpl commentFacade;

  @Test
  @DisplayName("루트 댓글 생성 성공")
  void createComment_success() {
    // given
    UUID postId = UUID.randomUUID();
    CustomPrincipal principal = mock(CustomPrincipal.class);
    CreateCommentRequestDto dto = new CreateCommentRequestDto("content", null);

    Comment comment = mock(Comment.class);
    CommentResponseDto responseDto = new CommentResponseDto(
        UUID.randomUUID(), "content", UUID.randomUUID(), "nickname", null, null
    );

    given(commentService.createComment(principal, postId, dto)).willReturn(comment);
    given(commentMapper.toResponse(comment)).willReturn(responseDto);

    // when
    CommentResponseDto result = commentFacade.createComment(principal, postId, dto);

    // then
    assertThat(result).isEqualTo(responseDto);
    then(commentService).should().createComment(principal, postId, dto);
    then(commentMapper).should().toResponse(comment);
  }

  @Test
  @DisplayName("게시물 댓글 조회 - 루트와 자식 매핑")
  void getPostComments_success() {
    // given
    UUID postId = UUID.randomUUID();

    // root mock
    Comment root = mock(Comment.class);
    UUID rootId = UUID.randomUUID();
    given(root.getId()).willReturn(rootId);

    // child mock
    Comment child = mock(Comment.class);
    UUID childId = UUID.randomUUID();
//    given(child.getId()).willReturn(childId);
    given(child.getParent()).willReturn(root);

    List<Comment> roots = List.of(root);
    List<Comment> children = List.of(child);

    PostCommentResponseDto rootDto = new PostCommentResponseDto(
        rootId, "root content", UUID.randomUUID(), "nick1", null, null, new java.util.ArrayList<>()
    );
    PostCommentResponseDto childDto = new PostCommentResponseDto(
        childId, "child content", UUID.randomUUID(), "nick2", null, rootId, new java.util.ArrayList<>()
    );

    given(commentService.getRootCommentsByPostId(postId)).willReturn(roots);
    given(commentService.getChildCommentsIn(roots)).willReturn(children);
    given(commentMapper.toPostCommentResponse(root)).willReturn(rootDto);
    given(commentMapper.toPostCommentResponse(child)).willReturn(childDto);

    // when
    List<PostCommentResponseDto> result = commentFacade.getPostComments(postId);

    // then
    assertThat(result).hasSize(1);
    PostCommentResponseDto parent = result.get(0);
    assertThat(parent.getCommentId()).isEqualTo(rootId);
    assertThat(parent.getChildComments()).containsExactly(childDto);

    then(commentService).should().getRootCommentsByPostId(postId);
    then(commentService).should().getChildCommentsIn(roots);
    then(commentMapper).should().toPostCommentResponse(root);
    then(commentMapper).should().toPostCommentResponse(child);
  }
}
