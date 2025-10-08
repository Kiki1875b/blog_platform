package com.example.blog.common.exception;

import lombok.Getter;

@Getter
public class AuthException extends DomainException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
