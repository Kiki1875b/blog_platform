package com.example.blog.domain.blog.respository;

import com.example.blog.domain.blog.dto.BlogPaginationRequest;
import com.example.blog.domain.blog.dto.BlogWithStat;
import com.example.blog.domain.blog.entity.Blog;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BlogRepositoryPort {
  Optional<Blog> findBySlug(String slug);
  Blog save(Blog blog);
  List<BlogWithStat> findByMemberIdAndQuery(UUID memberId, BlogPaginationRequest query);
  Blog findById(UUID blogId);
}
