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
import com.example.blog.domain.blog.dto.BlogWithStat;
import com.example.blog.domain.blog.dto.CreateBlogRequestDto;
import com.example.blog.domain.blog.dto.UpdateBlogRequestDto;
import com.example.blog.domain.blog.entity.Blog;
import com.example.blog.domain.blog.respository.BlogRepositoryPort;
import com.example.blog.domain.blog_tag.entity.BlogTag;
import com.example.blog.domain.blog_tag.repository.BlogTagRepository;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.repository.MemberRepositoryPort;
import com.example.blog.domain.tag.entity.Tag;
import com.example.blog.mapper.BlogMapper;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
  private final BlogTagRepository blogTagRepository; // TODO: port 로 분리

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
  public Blog updateBlog(UUID blogId, UpdateBlogRequestDto request, Member member, List<Tag> tags) {
    Blog blog = blogPort.findById(blogId);

    validateUserIsBlogOwner(blog, member);
    blog.updateTitle(request.title());
    blog.updateDescription(request.description());
    blog.updateVisibility(request.visibility());

    Set<Tag> incomingTags = new HashSet<>(tags);
    blog.updateTags(incomingTags);

    blogPort.save(blog);
    return blog;
  }

  @Override
  public void addTags(List<Tag> tags, Blog blog){
    blog.updateTags(tags);
  }

  @Override
  public Blog saveBlog(Blog blog){
    return blogPort.save(blog);
  }

  @Override
  @Transactional
  public PaginatedResponse<BlogResponseDto> getMemberBlogs(UUID memberId, BlogPaginationRequest request) {
    List<BlogWithStat> blogs = blogPort.findByMemberIdAndQuery(memberId, request);
    List<UUID> blogIds = extractBlogIds(blogs);
    List<BlogTag> blogTags = blogTagRepository.findAllByBlogIds(blogIds);
    PageInfo pageInfo = PaginationUtil.createPageForBlog(blogs, request.limit(), request.blogSortBy());

    List<BlogResponseDto> responseDtoList = blogMapper.toResponseListWithStat(blogs);
    return new PaginatedResponse<>(responseDtoList, pageInfo);
  }

  @Override
  public Blog findById(UUID uuid) {
    return blogPort.findById(uuid);
  }

  private void validateUserIsBlogOwner(Blog blog, Member member){
    if(!blog.getMember().equals(member)){
      throw new BlogException(ErrorCode.WRONG_BLOG_OWNER);
    }
  }

  private List<UUID> extractBlogIds(List<BlogWithStat> blogs) {
    return blogs.stream().map(BlogWithStat::blog).map(Blog::getId).toList();
  }
}
