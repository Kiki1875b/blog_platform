package com.example.blog.common.exception;


import lombok.Getter;

@Getter
public class MemberException extends IllegalArgumentException{
  private final ErrorCode errorCode;
  private final String detailMessage;

  public MemberException(ErrorCode errorCode){
    super(errorCode.getMessage());
    this.errorCode = errorCode;
    this.detailMessage = errorCode.getMessage();
  }
}
