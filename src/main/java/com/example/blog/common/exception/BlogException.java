package com.example.blog.common.exception;


import lombok.Getter;

@Getter
public class BlogException extends IllegalArgumentException{
  private final ErrorCode errorCode;
  private final String detailMessage;

  public BlogException(ErrorCode errorCode){
    super(errorCode.getMessage());
    this.errorCode = errorCode;
    this.detailMessage = errorCode.getMessage();
  }
}
