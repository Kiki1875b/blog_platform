package com.example.blog.common.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

  PASSWORD_MATCH_ERROR(HttpStatus.BAD_REQUEST, "비밀번호가 틀립니다."),
  USER_NOT_FOUND_BY_EMAIL(HttpStatus.NOT_FOUND, "해당 이메일의 사용자를 찾지 못했습니다."),
  REGISTER_EXCEPTION(HttpStatus.BAD_REQUEST, "회원가입에 실패했습니다.");

  private final HttpStatus status;
  private final String message;
}
