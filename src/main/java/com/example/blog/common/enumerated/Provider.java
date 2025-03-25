package com.example.blog.common.enumerated;

public enum Provider {
  GOOGLE, KAKAO, GITHUB;

  public static Provider from(String registrationId) {
    return switch (registrationId.toLowerCase()) {
      case "google" -> GOOGLE;
      case "kakao" -> KAKAO;
      case "github" -> GITHUB;
      default -> throw new IllegalArgumentException("지원하지 않는 Provider: " + registrationId);
    };
  }
}
