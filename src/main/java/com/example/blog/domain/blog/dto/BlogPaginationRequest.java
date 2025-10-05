package com.example.blog.domain.blog.dto;

import com.example.blog.common.pagenation.Direction;
import com.example.blog.common.pagenation.BlogSortBy;

public record BlogPaginationRequest(
    BlogSortBy blogSortBy,
    Direction direction,
    long limit,
    String cursor
) {
  public BlogPaginationRequest{
    if(blogSortBy == null) blogSortBy = BlogSortBy.FOLLOWERS;
    if(direction == null) direction = Direction.DESC;
    if(limit <= 0) limit = 10;
  }
}
