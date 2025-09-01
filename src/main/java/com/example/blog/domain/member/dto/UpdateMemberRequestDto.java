package com.example.blog.domain.member.dto;

import org.springframework.web.multipart.MultipartFile;

public record UpdateMemberRequestDto(
    String nickname,
    String password,
    String currentPassword,
    String s3Key
) {

}
