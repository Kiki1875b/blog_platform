package com.example.blog.domain.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.example.TestEntityFactory;
import com.example.blog.auth.user_details.CustomPrincipal;
import com.example.blog.domain.blog.entity.Blog;
import com.example.blog.domain.blog.service.BlogService;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.service.MemberService;
import com.example.blog.domain.post.dto.CreatePostRequestDto;
import com.example.blog.domain.post.dto.PostResponseDto;
import com.example.blog.domain.post.entity.Post;
import com.example.blog.domain.post.entity.PostState;
import com.example.blog.domain.post.facade.PostFacadeImpl;
import com.example.blog.domain.post.mapper.PostMapper;
import com.example.blog.domain.post.mapper.PostMapperImpl;
import com.example.blog.domain.post.service.PostCommandService;
import com.example.blog.domain.post_stat.service.PostStatService;
import com.example.blog.domain.tag.entity.Tag;
import com.example.blog.domain.tag.service.TagService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PostFacadeImplTest {

  @Mock
  private PostCommandService postCommandService;
  @Mock private BlogService blogService;
  @Mock private MemberService memberService;
  @Mock private TagService tagService;
  @Mock private PostStatService postStatService;
  private PostMapper postMapper = new PostMapperImpl();
  private PostFacadeImpl postFacade;


  private CustomPrincipal principal;
  private Member member;
  private Blog blog;
  private CreatePostRequestDto request;
  private Post post;
  private PostResponseDto responseDto;
  private UUID blogId;

  @BeforeEach
  void setUp(){
    postFacade = new PostFacadeImpl(postCommandService, blogService, memberService, tagService, postStatService, postMapper);
    principal = mock(CustomPrincipal.class);
    member = TestEntityFactory.createMember();
    blog = TestEntityFactory.createBlog(member);
    request = new CreatePostRequestDto(
        "title", "content", PostState.PUBLIC, List.of("t1")
    );

    post = TestEntityFactory.createPost(member, blog);
    responseDto = new PostResponseDto(
        post.getId(), blog.getId(), member.getId(), post.getTitle(), post.getContent(), post.getContentHtml(), post.getState(), post.getTagNames(), Instant.now()
    );
  }


  @Test
  @DisplayName("createPost 는 의도한 대로 실행된다")
  void createPost는_의도한대로_실행된다(){
    // given
    given(memberService.findMemberProxy(principal)).willReturn(member);
    given(blogService.findById(blogId)).willReturn(blog);
    given(tagService.getOrCreateTags(request.tags())).willReturn(List.of(new Tag("t1")));
    given(postCommandService.createPost(any(), any(), any(), any())).willReturn(post);

    // when
    PostResponseDto result = postFacade.createPost(principal, request, blogId);

    // then
    assertThat(result).isNotNull();
    assertThat(result.blogId()).isEqualTo(blog.getId());
    assertThat(result.authorId()).isEqualTo(member.getId());
    assertThat(result.title()).isEqualTo(post.getTitle());

    // 각 서비스 호출 검증
    then(memberService).should().findMemberProxy(principal);
    then(blogService).should().findById(blogId);
    then(tagService).should().getOrCreateTags(request.tags());

    // createPost 인자 검증
    ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
    ArgumentCaptor<Blog> blogCaptor = ArgumentCaptor.forClass(Blog.class);
    ArgumentCaptor<List<Tag>> tagsCaptor = ArgumentCaptor.forClass(List.class);
    ArgumentCaptor<CreatePostRequestDto> reqCaptor =
        ArgumentCaptor.forClass(CreatePostRequestDto.class);

    then(postCommandService)
        .should()
        .createPost(memberCaptor.capture(), blogCaptor.capture(), tagsCaptor.capture(), reqCaptor.capture());

    assertThat(memberCaptor.getValue()).isEqualTo(member);
    assertThat(blogCaptor.getValue()).isEqualTo(blog);
    assertThat(reqCaptor.getValue()).isEqualTo(request);

    // postStatService 호출 검증
    then(postStatService).should().createPostStat(post);
  }


}
