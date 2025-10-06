package com.example.blog.common.exception;

import lombok.Getter;

@Getter
public class DomainException extends IllegalArgumentException {

    private final ErrorCode errorCode;

    public DomainException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public DomainException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
