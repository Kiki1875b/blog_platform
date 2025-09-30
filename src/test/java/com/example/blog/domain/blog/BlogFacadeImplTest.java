package com.example.blog.domain.blog;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.example.blog.auth.user_details.CustomPrincipal;
import com.example.blog.common.pagenation.Direction;
import com.example.blog.common.pagenation.PageInfo;
import com.example.blog.common.pagenation.PaginatedResponse;
import com.example.blog.common.pagenation.BlogSortBy;
import com.example.blog.domain.blog.dto.BlogPaginationRequest;
import com.example.blog.domain.blog.dto.BlogResponseDto;
import com.example.blog.domain.blog.dto.CreateBlogRequestDto;
import com.example.blog.domain.blog.entity.Blog;
import com.example.blog.domain.blog.entity.BlogVisibility;
import com.example.blog.domain.blog.facade.BlogFacadeImpl;
import com.example.blog.domain.blog.service.BlogService;
import com.example.blog.domain.blog_stat.entity.BlogStat;
import com.example.blog.domain.blog_stat.service.BlogStatService;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.service.MemberService;
import com.example.blog.domain.tag.entity.Tag;
import com.example.blog.domain.tag.service.TagService;
import com.example.blog.mapper.BlogMapper;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BlogFacadeImplTest {


  @Mock private BlogService blogService;
  @Mock private TagService tagService;
  @Mock private BlogStatService blogStatService;
  @Mock private BlogMapper blogMapper;
  @Mock private MemberService memberService;
  @InjectMocks private BlogFacadeImpl blogFacade;

  private CreateBlogRequestDto createBlogRequestDto;
  private CustomPrincipal customPrincipal;
  private Blog blog;
  private List<Tag> tags;
  private BlogResponseDto blogResponseDto;
private UUID memberId;
  @BeforeEach
  void setUp(){
    memberId = UUID.randomUUID();
    createBlogRequestDto = new CreateBlogRequestDto(
        "title",
        List.of("tag1","tag2"),
        "description",
        "slug",
        BlogVisibility.PUBLIC
    );
    customPrincipal = new CustomPrincipal(memberId, "email", "USER", "ACTIVE" );
    blog = mock(Blog.class);
    tags = List.of(new Tag("tag1"), new Tag("tag2"));
    blogResponseDto = new BlogResponseDto(
        UUID.randomUUID(),
        memberId,
        createBlogRequestDto.title(),
        createBlogRequestDto.tags(),
        createBlogRequestDto.description(),
        createBlogRequestDto.visibility(),
        createBlogRequestDto.slug(),
        0L,
        0L,
        0L,
        Instant.now(),
        Instant.now()
    );
  }

  @Test
  @DisplayName("블로그 생성 오케스트레이션 정상 동작")
  void 블로그_생성_오케스트레이션_정상_동작(){
    // given
    given(blogService.createBlog(createBlogRequestDto, customPrincipal))
        .willReturn(blog);
    given(tagService.getOrCreateTags(createBlogRequestDto.tags()))
        .willReturn(tags);
    given(blogService.saveBlog(blog)).willReturn(blog);
    given(blogStatService.createBlogStat(blog)).willReturn(mock(BlogStat.class));
    given(blogMapper.toResponse(blog))
        .willReturn(blogResponseDto);

    //when
    BlogResponseDto res = blogFacade.createBlog(createBlogRequestDto, customPrincipal);

    //then
    verify(blogService).createBlog(createBlogRequestDto, customPrincipal);
    verify(tagService).getOrCreateTags(createBlogRequestDto.tags());
    verify(blogService).addTags(tags, blog);
    verify(blogService).saveBlog(blog);
    verify(blogStatService).createBlogStat(blog);
    verify(blogMapper).toResponse(blog);
  }

  @Test
  @DisplayName("멤버 검증 후 블로그 목록 조회 성공")
  void 멤버_검증_수_블로그_목록_조회_성공(){
    BlogPaginationRequest req = new BlogPaginationRequest(
        BlogSortBy.FOLLOWERS, Direction.DESC, 10, null
    );
    PaginatedResponse<BlogResponseDto> expected = new PaginatedResponse<>(List.of(blogResponseDto), new PageInfo(1, 10, 0, blogResponseDto.blogId().toString(), false));

    given(memberService.findMemberById(memberId))
        .willReturn(mock(Member.class));
    given(blogService.getMemberBlogs(memberId, req)).willReturn(expected);

    // when
    PaginatedResponse<BlogResponseDto> result =
        blogFacade.getMemberBlogs(memberId, req);

    // then
    Assertions.assertThat(result).isEqualTo(expected);

    verify(memberService).findMemberById(memberId);
    verify(blogService).getMemberBlogs(memberId, req);
    verifyNoMoreInteractions(blogService, tagService, blogStatService, blogMapper, memberService);
  }
}
