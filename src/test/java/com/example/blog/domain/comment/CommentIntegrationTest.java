package com.example.blog.domain.comment;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.PostgresContainerTest;
import com.example.blog.auth.jwt.JwtService;
import com.example.blog.domain.blog.entity.Blog;
import com.example.blog.domain.blog.respository.BlogRepository;
import com.example.blog.domain.comment.dto.CreateCommentRequestDto;
import com.example.blog.domain.comment.dto.UpdateCommentRequest;
import com.example.blog.domain.comment.entity.Comment;
import com.example.blog.domain.comment.repository.CommentRepository;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.repository.MemberRepository;
import com.example.blog.domain.post.entity.Post;
import com.example.blog.domain.post.repository.PostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CommentIntegrationTest extends PostgresContainerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BlogRepository blogRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;

    private Member member, otherMember;
    private Post post;
    private String accessToken, otherAccessToken;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
    @BeforeEach
    void setUp() {
        member = new Member("test@test.com", "password", "nickname", null, null, com.example.blog.common.enumerated.MemberStatus.ACTIVE, null, "name", com.example.blog.domain.member.entity.MemberRole.USER);
        memberRepository.save(member);

        otherMember = new Member("other@test.com", "password", "otherNickname", null, null, com.example.blog.common.enumerated.MemberStatus.ACTIVE, null, "name", com.example.blog.domain.member.entity.MemberRole.USER);
        memberRepository.save(otherMember);

        Blog blog = new Blog(member, "title", "desc", com.example.blog.domain.blog.entity.BlogVisibility.PUBLIC, "slug", null);
        blogRepository.save(blog);

        post = new Post(blog, member, "post title", "content", "html", com.example.blog.domain.post.entity.PostState.PUBLIC, null);
        postRepository.save(post);

        accessToken = jwtService.generateAccessToken(member, "USER");
        otherAccessToken = jwtService.generateAccessToken(otherMember, "USER");
    }

    @Test
    @DisplayName("게시물에 루트 댓글 작성")
    void createRootComment() throws Exception {
        CreateCommentRequestDto requestDto = new CreateCommentRequestDto("This is a root comment.", null);

        mockMvc.perform(post("/api/posts/{postId}/comments", post.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value(requestDto.content()))
                .andExpect(jsonPath("$.authorNickname").value(member.getNickname()))
                .andExpect(jsonPath("$.parentCommentId").isEmpty());
    }

    @Test
    @DisplayName("게시물에 대댓글 작성")
    void createNestedComment() throws Exception {
        Comment parentComment = new Comment(post, member, "parent content", "parent content",false, null);
        commentRepository.save(parentComment);

        CreateCommentRequestDto requestDto = new CreateCommentRequestDto("This is a nested comment.", parentComment.getId());

        mockMvc.perform(post("/api/posts/{postId}/comments", post.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value(requestDto.content()))
                .andExpect(jsonPath("$.authorNickname").value(member.getNickname()))
                .andExpect(jsonPath("$.parentCommentId").value(parentComment.getId().toString()));
    }

    @Test
    @DisplayName("인증 없이 댓글 작성 시 401 Unauthorized")
    void createComment_unauthorized_noToken() throws Exception {
        CreateCommentRequestDto requestDto = new CreateCommentRequestDto("Unauthorized comment.", null);

        mockMvc.perform(post("/api/posts/{postId}/comments", post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("유효하지 않은 토큰으로 댓글 작성 시 401 Unauthorized")
    void createComment_unauthorized_invalidToken() throws Exception {
        CreateCommentRequestDto requestDto = new CreateCommentRequestDto("Invalid token comment.", null);

        mockMvc.perform(post("/api/posts/{postId}/comments", post.getId())
                        .header("Authorization", "Bearer invalid_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("빈 내용으로 댓글 작성 시 400 Bad Request")
    void createComment_invalidInput_emptyContent() throws Exception {
        CreateCommentRequestDto requestDto = new CreateCommentRequestDto("", null);

        mockMvc.perform(post("/api/posts/{postId}/comments", post.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("존재하지 않는 게시물 ID로 댓글 작성 시 404 Not Found")
    void createComment_invalidInput_nonExistentPostId() throws Exception {
        UUID nonExistentPostId = UUID.randomUUID();
        CreateCommentRequestDto requestDto = new CreateCommentRequestDto("Comment for non-existent post.", null);

        mockMvc.perform(post("/api/posts/{postId}/comments", nonExistentPostId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("존재하지 않는 부모 댓글 ID로 대댓글 작성 시 404 Not Found")
    void createComment_invalidInput_nonExistentParentCommentId() throws Exception {
        UUID nonExistentParentCommentId = UUID.randomUUID();
        CreateCommentRequestDto requestDto = new CreateCommentRequestDto("Nested comment for non-existent parent.", nonExistentParentCommentId);

        mockMvc.perform(post("/api/posts/{postId}/comments", post.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("댓글 수정 - 성공")
    void updateComment_success() throws Exception {
        Comment comment = new Comment(post, member, "original content", "parent content",false, null);
        commentRepository.save(comment);

        UpdateCommentRequest requestDto = new UpdateCommentRequest("updated content");

        mockMvc.perform(patch("/api/comments/{commentId}", comment.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(requestDto.content()));
    }

    @Test
    @DisplayName("댓글 수정 - 실패 (미인증)")
    void updateComment_fail_unauthorized() throws Exception {
        Comment comment = new Comment(post, member, "original content", "parent content",false, null);
        commentRepository.save(comment);

        UpdateCommentRequest requestDto = new UpdateCommentRequest("updated content");

        mockMvc.perform(patch("/api/comments/{commentId}", comment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("댓글 수정 - 실패 (권한 없음)")
    void updateComment_fail_forbidden() throws Exception {
        Comment comment = new Comment(post, member, "original content", "parent content",false, null);
        commentRepository.save(comment);

        UpdateCommentRequest requestDto = new UpdateCommentRequest("updated content");

        mockMvc.perform(patch("http://localhost:8080/api/comments/{commentId}", comment.getId())
                        .header("Authorization", "Bearer " + otherAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("댓글 삭제 - 성공")
    void deleteComment_success() throws Exception {
        Comment comment = new Comment(post, member, "original content", "parent content",false, null);
        commentRepository.save(comment);

        mockMvc.perform(delete("/api/comments/{commentId}", comment.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("댓글 삭제 - 실패 (미인증)")
    void deleteComment_fail_unauthorized() throws Exception {
        Comment comment = new Comment(post, member, "original content", "parent content",false, null);
        commentRepository.save(comment);

        mockMvc.perform(delete("/api/comments/{commentId}", comment.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("댓글 삭제 - 실패 (권한 없음)")
    void deleteComment_fail_forbidden() throws Exception {
        Comment comment = new Comment(post, member, "original content", "parent content",false, null);
        commentRepository.save(comment);

        mockMvc.perform(delete("/api/comments/{commentId}", comment.getId())
                        .header("Authorization", "Bearer " + otherAccessToken))
                .andExpect(status().isUnauthorized());
    }
}
