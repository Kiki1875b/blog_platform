package com.example.blog.common.exception;

import lombok.Getter;

@Getter
public class CommentException extends BlogException {
    public CommentException(ErrorCode errorCode) {
        super(errorCode);
    }
}
