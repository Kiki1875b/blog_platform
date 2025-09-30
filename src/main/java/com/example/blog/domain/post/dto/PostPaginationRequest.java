package com.example.blog.domain.post.dto;

import com.example.blog.common.pagenation.Direction;
import com.example.blog.common.pagenation.PostSortBy;

public record PostPaginationRequest(
    PostSortBy postSortBy,
    Direction direction,
    Long limit,
    String cursor
) {
  public PostPaginationRequest{
    if(postSortBy == null) postSortBy = PostSortBy.DATE;
    if(direction == null) direction = Direction.DESC;
    if (limit == null || limit <= 0) limit = 10L;
  }
}
