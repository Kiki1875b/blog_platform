package com.example.blog.domain.blog.respository;

import com.example.blog.domain.blog.dto.BlogPaginationRequest;
import com.example.blog.domain.blog.entity.Blog;
import java.util.List;
import java.util.UUID;

public interface BlogQueryRepository {
  List<Blog> findByMemberIdAndQuery(UUID memberId, BlogPaginationRequest request);
}
