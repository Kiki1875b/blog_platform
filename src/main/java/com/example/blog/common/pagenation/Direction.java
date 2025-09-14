package com.example.blog.common.pagenation;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Direction {
  DESC, ASC;

  @JsonCreator
  public static Direction from(String value) {
    return Direction.valueOf(value.toUpperCase());
  }
}
