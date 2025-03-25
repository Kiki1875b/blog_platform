package com.example.blog.auth.controller;

import com.example.blog.domain.member.entity.Member;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
  @GetMapping("/api/private/check-auth")
  public ResponseEntity<String> checkAuth(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
    }

    Member principal = (Member) authentication.getPrincipal();
    return ResponseEntity.ok("인증된 사용자입니다. principal = " + principal.toString());
  }
}
