package com.example.blog.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        log.error("DomainException: {}", ex.getMessage());
        return ResponseEntity.status(errorCode.getStatus()).body(new ErrorResponse(errorCode.getStatus().value(),
            errorCode.getMessage()));
    }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
        errors.put(error.getField(), error.getDefaultMessage())
    );

    return ResponseEntity.badRequest().body(errors);
  }

  // 존재하지 않는 엔드포인트 요청 시
  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(NoHandlerFoundException ex,
      HttpServletRequest request) {
    ErrorResponse error = new ErrorResponse(
        HttpStatus.NOT_FOUND.value(),
        "잘못된 요청입니다. 요청한 URL: " + request.getRequestURI()
    );
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  // 기타 예외
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception ex) {

    log.error("예외 발생:[ {} ]", ex.getMessage());
    ErrorResponse error = new ErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "서버 내부 오류가 발생했습니다."
    );
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
