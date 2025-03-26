package com.example.blog.domain.member;

import com.example.blog.common.enumerated.MemberStatus;
import com.example.blog.domain.member.entity.MemberRole;
import java.time.Instant;
import lombok.Setter;


public record MemberResponseDto(
    Long id,
    Instant createdAt,
    Instant updatedAt,
    String email,
    String nickname,
    String provider,
    MemberStatus status,
    String profileUrl,
    String name,
    MemberRole role
) {

}
