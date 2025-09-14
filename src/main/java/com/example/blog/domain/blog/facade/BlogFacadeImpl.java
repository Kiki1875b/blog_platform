package com.example.blog.domain.blog.facade;

import com.example.blog.auth.user_details.CustomPrincipal;
import com.example.blog.common.pagenation.PaginatedResponse;
import com.example.blog.domain.blog.dto.BlogPaginationRequest;
import com.example.blog.domain.blog.dto.BlogResponseDto;
import com.example.blog.domain.blog.dto.CreateBlogRequestDto;
import com.example.blog.domain.blog.dto.UpdateBlogRequestDto;
import com.example.blog.domain.blog.entity.Blog;
import com.example.blog.domain.blog.service.BlogService;
import com.example.blog.domain.blog_stat.service.BlogStatService;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.service.MemberService;
import com.example.blog.domain.tag.entity.Tag;
import com.example.blog.domain.tag.service.TagService;
import com.example.blog.mapper.BlogMapper;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class BlogFacadeImpl implements BlogFacade{

  private final BlogService blogService;
  private final TagService tagService;
  private final BlogStatService blogStatService;
  private final BlogMapper blogMapper;
  private final MemberService memberService;

  @Override
  public BlogResponseDto createBlog(CreateBlogRequestDto request, CustomPrincipal principal) {
    Blog blog = blogService.createBlog(request, principal);
    List<Tag> tags = tagService.getOrCreateTags(request.tags());
    blogService.addTags(tags, blog);
    Blog saved = blogService.saveBlog(blog);
    blogStatService.createBlogStat(blog); // TODO: 이벤트 분리

    return blogMapper.toResponse(blog);
  }

  @Override
  public PaginatedResponse<BlogResponseDto> getMemberBlogs(UUID memberId,
      BlogPaginationRequest request) {

    memberService.findMemberById(memberId);
    return blogService.getMemberBlogs(memberId, request);
  }

  @Override
  @Transactional
  public BlogResponseDto updateBlog(UUID blogId, UpdateBlogRequestDto request,
      CustomPrincipal principal) {
    // 사용자 조회
    Member member = memberService.findMemberById(principal.id());

    // 테그를 생성하거나 기존 테그 가져온 후
    List<Tag> tags = tagService.getOrCreateTags(request.tags());

    // 업데이트
    Blog blog = blogService.updateBlog(blogId, request, member, tags);

    // blogstat 불러와야 할 수도

    return blogMapper.toResponse(blog);
  }
}
