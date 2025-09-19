package com.example.blog.domain.post;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;

import com.example.TestEntityFactory;
import com.example.blog.common.markdown.MarkdownService;
import com.example.blog.domain.blog.entity.Blog;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.post.dto.CreatePostRequestDto;
import com.example.blog.domain.post.entity.Post;
import com.example.blog.domain.post.entity.PostState;
import com.example.blog.domain.post.mapper.PostMapper;
import com.example.blog.domain.post.repository.PostRepositoryPort;
import com.example.blog.domain.post.service.PostCommandServiceImpl;
import com.example.blog.domain.tag.entity.Tag;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class PostCommandServiceImplTest {
  @Mock
  private MarkdownService mdService;
  @Mock private PostMapper postMapper;
  @Mock private PostRepositoryPort postPort;

  private PostCommandServiceImpl postCommandService;

  private Member member;
  private Blog blog;
  private CreatePostRequestDto request;
  private Post post;

  @BeforeEach
  void setUp() {
    postCommandService = new PostCommandServiceImpl(mdService, postMapper, postPort);

    member = TestEntityFactory.createMember();
    blog = TestEntityFactory.createBlog(member);
    request =
        new CreatePostRequestDto(
            "title",
            "some **markdown** content",
            PostState.PUBLIC,
            List.of("t1", "t2"));

    post = TestEntityFactory.createPost(member, blog);
  }

  @Test
  @DisplayName("createPost 는 Post를 생성하고 markdown 을 html 로 변환 후 저장한다")
  void createPost는_Post를_생성하고_html변환_후_저장한다() {
    // given
    given(postMapper.toEntity(request)).willReturn(post);
    given(mdService.toHtml(request.content())).willReturn("<p>some <b>markdown</b> content</p>");
    given(postPort.save(any(Post.class))).willAnswer(invocation -> invocation.getArgument(0));

    // when
    Tag t1 = new Tag("t1");
    Tag t2 = new Tag("t2");
    ReflectionTestUtils.setField(t1, "id", UUID.randomUUID());
    ReflectionTestUtils.setField(t2, "id", UUID.randomUUID());

    Post result = postCommandService.createPost(member, blog, List.of(t1, t2), request);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getMember()).isEqualTo(member);
    assertThat(result.getBlog()).isEqualTo(blog);
    assertThat(result.getTagNames()).containsExactlyInAnyOrder("t1", "t2");
    assertThat(result.getContentHtml()).contains("<p>").contains("markdown");

    // 호출 순서/내용 검증
    then(postMapper).should(times(1)).toEntity(request);
    then(mdService).should(times(1)).toHtml(request.content());

    // save 된 Post 검증
    ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
    then(postPort).should(times(1)).save(postCaptor.capture());
    Post saved = postCaptor.getValue();

    assertThat(saved.getMember()).isEqualTo(member);
    assertThat(saved.getBlog()).isEqualTo(blog);
    assertThat(saved.getTagNames()).contains("t1", "t2");
  }
}
