package com.example.blog.domain.post;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.PostgresContainerTest;
import com.example.blog.auth.jwt.JwtService;
import com.example.blog.common.enumerated.MemberStatus;
import com.example.blog.domain.blog.entity.Blog;
import com.example.blog.domain.blog.entity.BlogVisibility;
import com.example.blog.domain.blog.respository.BlogRepository;
import com.example.blog.domain.blog_tag.entity.BlogTag;
import com.example.blog.domain.blog_tag.repository.BlogTagRepository;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.entity.MemberRole;
import com.example.blog.domain.member.repository.MemberRepository;
import com.example.blog.domain.post.dto.CreatePostRequestDto;
import com.example.blog.domain.post.entity.Post;
import com.example.blog.domain.post.entity.PostState;
import com.example.blog.domain.post.repository.PostRepository;
import com.example.blog.domain.tag.entity.Tag;
import com.example.blog.domain.tag.repository.TagRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)  // 추가

public class PostIntegrationTest extends PostgresContainerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private PostRepository postRepository;
  @Autowired private MemberRepository memberRepository;
  @Autowired private BlogRepository blogRepository;
  @Autowired private TagRepository tagRepository;
  @Autowired private BlogTagRepository blogTagRepository;
  @Autowired private ObjectMapper mapper;
  @Autowired private JwtService jwtService;

  private Member member;
  private Blog memberBlog;
  private Tag tag;
  private BlogTag blogTag;

  private Post post;
  @BeforeEach
  void setUp(){
    member = new Member(
        "email@email.com",
        "pwd",
        "nickname",
        null,
        null,
        MemberStatus.ACTIVE,
        null,
        "name",
        MemberRole.USER
    );

    memberRepository.save(member);

    tag = new Tag("tag1");
    tagRepository.save(tag);

    memberBlog = new Blog(
        member, "title", "description", BlogVisibility.PUBLIC, "slug", null
    );

    memberBlog.updateTags(List.of(tag));
    blogRepository.save(memberBlog);

    post = new Post(memberBlog, member, "postTitle", "## Post Header", "<h2>Post Header</h2>", PostState.PUBLIC, null);
    post.updateTags(Set.of(tag));
    postRepository.save(post);
  }

  @Test
  @DisplayName("블로그에 새 게시글 작성")
  void createBlogPost() throws Exception{
    String jwt = jwtService.generateAccessToken(member, "ROLE_USER");

    List<String> tagStringList = new ArrayList<>();
    tagStringList.add("tag2");
    CreatePostRequestDto request = new CreatePostRequestDto(
        "post", "## content", PostState.PUBLIC, tagStringList
    );

    mockMvc.perform(post("/api/blogs/{blogId}/posts", memberBlog.getId())
        .header("Authorization", "Bearer " + jwt)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("post"))
        .andExpect(jsonPath("$.tags").isArray());

    Assertions.assertThat(postRepository.findAll())
        .extracting(Post::getTitle)
        .contains("post");
  }
  @Test
  @DisplayName("게시글 단건 조회")
  void getPostById() throws Exception {

    mockMvc.perform(get("/api/posts/{postId}", post.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.postId").value(post.getId().toString()))
        .andExpect(jsonPath("$.authorId").value(member.getId().toString()))
        .andExpect(jsonPath("$.blogId").value(memberBlog.getId().toString()))
        .andExpect(jsonPath("$.title").value(post.getTitle()));
  }
}
