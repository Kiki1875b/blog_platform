package com.example.blog.common.exception;


import lombok.Getter;

@Getter
public class PostException extends DomainException {

    public PostException(ErrorCode errorCode) {
        super(errorCode);
    }

    public PostException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
