package com.example.blog.domain.blog.facade;

import com.example.blog.auth.user_details.CustomPrincipal;
import com.example.blog.common.pagenation.PaginatedResponse;
import com.example.blog.domain.blog.dto.BlogPaginationRequest;
import com.example.blog.domain.blog.dto.BlogResponseDto;
import com.example.blog.domain.blog.dto.CreateBlogRequestDto;
import com.example.blog.domain.blog.entity.Blog;
import com.example.blog.domain.blog.service.BlogService;
import com.example.blog.domain.blog_stat.service.BlogStatService;
import com.example.blog.domain.member.service.MemberService;
import com.example.blog.domain.tag.entity.Tag;
import com.example.blog.domain.tag.service.TagService;
import com.example.blog.mapper.BlogMapper;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
}
