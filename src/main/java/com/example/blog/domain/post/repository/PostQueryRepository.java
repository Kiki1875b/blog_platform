package com.example.blog.domain.post.repository;

import com.example.blog.domain.post.dto.PostPaginationRequest;
import com.example.blog.domain.post.dto.PostWithStat;
import java.util.List;
import java.util.UUID;

public interface PostQueryRepository {
  List<PostWithStat> findByBlogIdAndQuery(UUID blogId, PostPaginationRequest request);
}
