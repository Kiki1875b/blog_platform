package com.example.blog.auth.dto;

public record RegisterRequestDTO(
    String email,
    String password,
    String checkPassword,
    String nickname,
    String name
) {
}
