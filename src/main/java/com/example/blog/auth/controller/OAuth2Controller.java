package com.example.blog.auth.controller;


import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class OAuth2Controller {

  @Value("${custom.oauth2.base-url}")
  private String baseUrl;

  @GetMapping("/api/oauth2/authorize/{provider}")
  public void authorize(@PathVariable String provider, HttpServletResponse response){
    String redirectUri = baseUrl + "/oauth2/authorization/" + provider;
    try {
      response.sendRedirect(redirectUri);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
