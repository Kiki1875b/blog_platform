package com.example.blog.common.exception;


import lombok.Getter;

@Getter
public class BlogException extends DomainException {

    public BlogException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BlogException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
