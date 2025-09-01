package com.example.blog.common.aws.s3;

public record PresignRequestDto(
    String fileName,
    String contentType
) {

}
