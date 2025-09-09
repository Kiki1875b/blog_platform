package com.example.blog.domain.blog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import com.example.TestEntityFactory;
import com.example.blog.auth.user_details.CustomPrincipal;
import com.example.blog.common.exception.BlogException;
import com.example.blog.common.exception.MemberException;
import com.example.blog.domain.blog.dto.CreateBlogRequestDto;
import com.example.blog.domain.blog.entity.Blog;
import com.example.blog.domain.blog.entity.BlogVisibility;
import com.example.blog.domain.blog.respository.BlogRepositoryPort;
import com.example.blog.domain.blog.service.BlogServiceImpl;
import com.example.blog.domain.blog_stat.service.BlogStatService;
import com.example.blog.domain.blog_tag.repository.BlogTagRepository;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.repository.MemberRepositoryPort;
import com.example.blog.domain.tag.entity.Tag;
import com.example.blog.domain.tag.service.TagService;
import com.example.blog.mapper.BlogMapper;
import com.example.blog.mapper.BlogMapperImpl;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BlogServiceImplTest {

  @Mock private MemberRepositoryPort memberPort;
  @Mock private BlogRepositoryPort blogPort;
  private BlogMapper blogMapper = new BlogMapperImpl();
  @Mock private TagService tagService;
  @Mock private BlogTagRepository blogTagRepository;
  @Mock   private BlogStatService blogStatService;

  private BlogServiceImpl blogService;

  Member member;
  CustomPrincipal principal;
  CreateBlogRequestDto requestDto;
  @BeforeEach
  void setUp() {
    member = TestEntityFactory.createMember();
    principal = mock(CustomPrincipal.class);
    requestDto = new CreateBlogRequestDto(
        "title",
        List.of("tag1"),
        "description",
        "slug",
        BlogVisibility.PUBLIC
    );
    blogService = new BlogServiceImpl(memberPort, blogPort, blogMapper);
  }

  @Test
  @DisplayName("블로그 정상 생성")
  void 블로그를_생성할_수_있다(){
    // given
    UUID randomId = member.getId();

    given(principal.id()).willReturn(randomId);

    given(blogPort.findBySlug(requestDto.slug())).willReturn(Optional.empty());
    given(memberPort.findById(randomId)).willReturn(Optional.of(member));

    Tag tag = new Tag(requestDto.tags().get(0));
//    given(tagService.getOrCreateTags(requestDto.tags())).willReturn(List.of(tag));

    Blog blogEntity = mock(Blog.class);
//    given(blogPort.save(any(Blog.class))).willAnswer(invocation -> invocation.getArgument(0));

    // when
    Blog result = blogService.createBlog(requestDto, principal);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getTitle()).isEqualTo("title");
    assertThat(result.getSlug()).isEqualTo("slug");
//    assertThat(result.tags()).containsExactly("tag1");
  }

  @Test
  @DisplayName("동일 slug 존재시 예외")
  void 동일_slug_존재시_예외(){
    // given
    given(blogPort.findBySlug(requestDto.slug())).willReturn(Optional.of(mock(Blog.class)));

    // when & then
    assertThatThrownBy(() -> blogService.createBlog(requestDto, principal))
        .isInstanceOf(BlogException.class);

    verifyNoInteractions(memberPort);
    verifyNoInteractions(tagService);
  }

  @Test
  @DisplayName("Member 미존재시 예외")
  void Member존재하지않을시_예외(){
    // given
    UUID randomId = member.getId();
    given(principal.id()).willReturn(randomId);
    given(blogPort.findBySlug(requestDto.slug())).willReturn(Optional.empty());
    given(memberPort.findById(randomId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> blogService.createBlog(requestDto, principal))
        .isInstanceOf(MemberException.class);
    verifyNoInteractions(tagService);
  }
}
