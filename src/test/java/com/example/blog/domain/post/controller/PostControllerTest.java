package com.example.blog.domain.post.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.PostgresContainerTest;
import com.example.blog.domain.post.dto.PostResponseDto;
import com.example.blog.domain.post.dto.UpdatePostRequestDto;
import com.example.blog.domain.post.entity.PostState;
import com.example.blog.domain.post.facade.PostFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)  // 추가
public class PostControllerTest extends PostgresContainerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostFacade postFacade;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void updatePost() throws Exception {
        // given
        UUID postId = UUID.randomUUID();
        UpdatePostRequestDto request = new UpdatePostRequestDto("새 게시글", "# Markdown Content", PostState.PUBLIC, List.of("string", "string"));
        PostResponseDto response = new PostResponseDto(
                postId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "새 게시글",
                "# Markdown Content",
                "<h1>Markdown Content</h1>",
                PostState.PUBLIC,
                List.of("string", "string"),
                Instant.now(),
                0L,
                0L
        );

        given(postFacade.updatePost(any(), eq(postId), any(UpdatePostRequestDto.class))).willReturn(response);

        // when & then
        mockMvc.perform(patch("/api/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(postId.toString()))
                .andExpect(jsonPath("$.title").value("새 게시글"))
                .andExpect(jsonPath("$.content").value("# Markdown Content"))
                .andExpect(jsonPath("$.status").value("PUBLIC"));
    }
}
