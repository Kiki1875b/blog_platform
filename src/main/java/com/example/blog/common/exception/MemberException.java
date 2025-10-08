package com.example.blog.common.exception;


import lombok.Getter;

@Getter
public class MemberException extends DomainException {

    public MemberException(ErrorCode errorCode) {
        super(errorCode);
    }

    public MemberException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
