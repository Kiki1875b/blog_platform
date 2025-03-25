package com.example.blog.common.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

  REGISTER_EXCEPTION(HttpStatus.BAD_REQUEST, "회원가입에 실패했습니다.");

  private final HttpStatus status;
  private final String message;
}
