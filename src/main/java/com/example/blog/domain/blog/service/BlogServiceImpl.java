package com.example.blog.domain.blog.service;

import com.example.blog.auth.user_details.CustomPrincipal;
import com.example.blog.common.exception.BlogException;
import com.example.blog.common.exception.ErrorCode;
import com.example.blog.common.exception.MemberException;
import com.example.blog.common.pagenation.PageInfo;
import com.example.blog.common.pagenation.PaginatedResponse;
import com.example.blog.common.pagenation.PaginationUtil;
import com.example.blog.domain.blog.dto.BlogPaginationRequest;
import com.example.blog.domain.blog.dto.BlogResponseDto;
import com.example.blog.domain.blog.dto.CreateBlogRequestDto;
import com.example.blog.domain.blog.entity.Blog;
import com.example.blog.domain.blog.respository.BlogRepositoryPort;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.repository.MemberRepositoryPort;
import com.example.blog.domain.tag.entity.Tag;
import com.example.blog.mapper.BlogMapper;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService{

  private final MemberRepositoryPort memberPort;
  private final BlogRepositoryPort blogPort;
  private final BlogMapper blogMapper;

  @Override
  @Transactional
  public Blog createBlog(CreateBlogRequestDto request, CustomPrincipal principal) {

    if (blogPort.findBySlug(request.slug()).isPresent()) {
      throw new BlogException(ErrorCode.DUPLICATE_SLUG_EXCEPTION);
    }

    Member member = memberPort.findById(principal.id())
        .orElseThrow(() -> new MemberException(ErrorCode.USER_NOT_FOUND));

    return blogMapper.toEntity(request, member);
  }

  @Override
  public void addTags(List<Tag> tags, Blog blog){
    blog.addTags(tags);
  }

  @Override
  public Blog saveBlog(Blog blog){
    return blogPort.save(blog);
  }

  @Override
  @Transactional
  public PaginatedResponse<BlogResponseDto> getMemberBlogs(UUID memberId, BlogPaginationRequest request) {
    List<Blog> blogs = blogPort.findByMemberIdAndQuery(memberId, request);
    PageInfo pageInfo = PaginationUtil.createPageInfo(blogs, request.limit());
    List<BlogResponseDto> responseDtoList = blogMapper.toResponseList(blogs);
    return new PaginatedResponse<>(responseDtoList, pageInfo);
  }
}
