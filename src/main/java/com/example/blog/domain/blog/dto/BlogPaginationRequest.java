package com.example.blog.domain.blog.dto;

import com.example.blog.common.pagenation.Direction;
import com.example.blog.common.pagenation.SortBy;
import java.util.UUID;

public record BlogPaginationRequest(
    SortBy sortBy,
    Direction direction,
    long limit,
    UUID cursor
) {
  public BlogPaginationRequest{
    if(sortBy == null) sortBy = SortBy.FOLLOWERS;
    if(direction == null) direction = Direction.DESC;
    if(limit <= 0) limit = 10;
  }
}
