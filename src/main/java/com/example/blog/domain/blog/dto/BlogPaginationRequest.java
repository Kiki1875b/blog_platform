package com.example.blog.domain.blog.dto;

import com.example.blog.common.pagenation.Direction;
import com.example.blog.common.pagenation.SortBy;

public record BlogPaginationRequest(
    SortBy sortBy,
    Direction direction,
    long limit,
    String cursor
) {
  public BlogPaginationRequest{
    if(sortBy == null) sortBy = SortBy.FOLLOWERS;
    if(direction == null) direction = Direction.DESC;
    if(limit <= 0) limit = 10;
  }
}
