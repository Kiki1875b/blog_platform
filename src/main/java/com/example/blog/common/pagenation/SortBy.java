package com.example.blog.common.pagenation;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SortBy {
  FOLLOWERS, POSTS, VIEWS;

  @JsonCreator
  public static SortBy from(String value) {
    return SortBy.valueOf(value.toUpperCase());
  }
}
