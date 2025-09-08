package com.example.blog.domain.blog.respository;

import com.example.blog.domain.blog.entity.Blog;
import java.util.Optional;
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
}
