package com.example.blog.domain.blog.service;

import com.example.blog.auth.user_details.CustomPrincipal;
import com.example.blog.common.exception.BlogException;
import com.example.blog.common.exception.ErrorCode;
import com.example.blog.common.exception.MemberException;
import com.example.blog.domain.blog.dto.BlogResponseDto;
import com.example.blog.domain.blog.dto.CreateBlogRequestDto;
import com.example.blog.domain.blog.entity.Blog;
import com.example.blog.domain.blog.respository.BlogRepository;
import com.example.blog.domain.blog.respository.BlogRepositoryAdapter;
import com.example.blog.domain.blog.respository.BlogRepositoryPort;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.repository.MemberRepositoryAdapter;
import com.example.blog.domain.member.repository.MemberRepositoryPort;
import com.example.blog.domain.tag.entity.Tag;
import com.example.blog.domain.tag.service.TagService;
import com.example.blog.mapper.BlogMapper;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService{

  private final MemberRepositoryPort memberPort;
  private final BlogRepositoryPort blogPort;
  private final BlogMapper blogMapper;
  private final TagService tagService;
  @Override
  @Transactional
  public BlogResponseDto createBlog(CreateBlogRequestDto request, CustomPrincipal principal) {

    if (blogPort.findBySlug(request.slug()).isPresent()) {
      throw new BlogException(ErrorCode.DUPLICATE_SLUG_EXCEPTION);
    }

    Member member = memberPort.findById(principal.id())
        .orElseThrow(() -> new MemberException(ErrorCode.USER_NOT_FOUND));
    List<Tag> tags = tagService.createTags(request.tags());
    List<String> tagStrings = tags.stream().map(Tag::getName).toList();

    Blog blog = blogMapper.toEntity(request, member);
    blog.addTags(tags);
    Blog saved = blogPort.save(blog);

    return blogMapper.toResponse(blog, tagStrings);

  }
}
