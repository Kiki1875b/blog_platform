//package com.example.blog.domain.comment.mapper;
//
//import com.example.TestEntityFactory;
//import com.example.blog.domain.comment.dto.CommentResponseDto;
//import com.example.blog.domain.comment.dto.CreateCommentRequestDto;
//import com.example.blog.domain.comment.entity.Comment;
//import com.example.blog.domain.member.entity.Member;
//import com.example.blog.domain.post.entity.Post;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mapstruct.factory.Mappers;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//class CommentMapperTest {
//
//    private final CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);
//
//    @Test
//    @DisplayName("CreateCommentRequestDto를 Comment 엔티티로 매핑")
//    void toEntity() {
//        // given
//        CreateCommentRequestDto dto = new CreateCommentRequestDto("test content", null);
//        Post post = TestEntityFactory.createPost(null, null);
//        Member member = TestEntityFactory.createMember();
//
//        // when
//        Comment comment = commentMapper.toEntity(dto, post, member, null);
//
//        // then
//        assertThat(comment.getContent()).isEqualTo(dto.content());
//        assertThat(comment.getPost()).isEqualTo(post);
//        assertThat(comment.getMember()).isEqualTo(member);
//        assertThat(comment.getParent()).isNull();
//        assertThat(comment.isDeleted()).isFalse();
//    }
//
//    @Test
//    @DisplayName("Comment 엔티티를 CommentResponseDto로 매핑")
//    void toResponse() {
//        // given
//        Member member = TestEntityFactory.createMember();
//        Post post = TestEntityFactory.createPost(member, null);
//        Comment comment = TestEntityFactory.createComment(post, member, (Comment) null);
//
//        // when
//        CommentResponseDto dto = commentMapper.toResponse(comment);
//
//        // then
//        assertThat(dto.commentId()).isEqualTo(comment.getId());
//        assertThat(dto.content()).isEqualTo(comment.getContent());
//        assertThat(dto.authorId()).isEqualTo(member.getId());
//        assertThat(dto.authorNickname()).isEqualTo(member.getNickname());
//        assertThat(dto.parentCommentId()).isNull();
//        assertThat(dto.createdAt()).isEqualTo(comment.getCreatedAt());
//    }
//}
