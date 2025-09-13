package com.example.blog.domain.blog.respository;

import com.example.blog.common.exception.BlogException;
import com.example.blog.common.exception.ErrorCode;
import com.example.blog.domain.blog.dto.BlogPaginationRequest;
import com.example.blog.domain.blog.dto.BlogWithStat;
import com.example.blog.domain.blog.entity.Blog;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class BlogRepositoryAdapter implements BlogRepositoryPort{

  private final BlogRepository blogRepository;

  @Override
  public Optional<Blog> findBySlug(String slug) {
    return blogRepository.findBySlug(slug);
  }

  @Override
  public Blog save(Blog blog) {
    return blogRepository.save(blog);
  }

  @Override
  public List<BlogWithStat> findByMemberIdAndQuery(UUID memberId, BlogPaginationRequest query) {
    return blogRepository.findByMemberIdAndQuery(memberId, query);
  }

  @Override
  public Blog findById(UUID blogId) {
    return blogRepository.findById(blogId).orElseThrow(() -> new BlogException(ErrorCode.BLOG_NOT_FOUND));
  }
}
