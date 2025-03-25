package com.example.blog.auth.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class LoginSuccessController {

  @GetMapping("/api/public/loginSuccess")
  public String loginSuccess(@AuthenticationPrincipal OAuth2User oAuth2User) {
    if (oAuth2User == null) {
      return "OAuth2 로그인 실패";
    }
    log.info("로그인한 사용자 정보: {}", oAuth2User.getAttributes());
    return "OAuth2 로그인 성공: " + oAuth2User.getAttribute("email");
  }
}
