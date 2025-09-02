package com.example.blog.common.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

  //Auth Exception
  INVALID_TOKEN_ERROR(HttpStatus.UNAUTHORIZED, "유효하지 않은 token 입니다."),
  ALREADY_REGISTERED_EMAIL(HttpStatus.BAD_REQUEST, "이미 등록된 이메일 입니다."),
  PASSWORD_MATCH_ERROR(HttpStatus.BAD_REQUEST, "비밀번호가 틀립니다."),

  // USER
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"),
  INVALID_S3_PROFILE_KEY(HttpStatus.BAD_REQUEST, "올바르지 않은 s3Key 입니다."),
  USER_NOT_FOUND_BY_EMAIL(HttpStatus.NOT_FOUND, "해당 이메일의 사용자를 찾지 못했습니다."),
  REGISTER_EXCEPTION(HttpStatus.BAD_REQUEST, "회원가입에 실패했습니다.");

  private final HttpStatus status;
  private final String message;
}
