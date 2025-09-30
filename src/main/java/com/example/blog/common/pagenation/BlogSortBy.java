package com.example.blog.common.pagenation;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum BlogSortBy {
  FOLLOWERS, POSTS, VIEWS;

  @JsonCreator
  public static BlogSortBy from(String value) {
    return BlogSortBy.valueOf(value.toUpperCase());
  }
}
