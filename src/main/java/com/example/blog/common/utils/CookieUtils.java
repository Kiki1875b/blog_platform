package com.example.blog.common.utils;

import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import org.springframework.http.ResponseCookie;

public final class CookieUtils {
  private CookieUtils() {}
  public static final String REFRESH_COOKIE = "REFRESH_TOKEN";

  public static void clearAllRefreshCookies(HttpServletResponse res, boolean secure, String sameSite) {
    String[] paths = {"/", "/api", "/api/auth", "/api/auth/refresh"};
    for (String p : paths) {
      res.addHeader("Set-Cookie", ResponseCookie.from(REFRESH_COOKIE, "")
          .httpOnly(true).secure(secure).sameSite(sameSite).path(p).maxAge(0).build().toString());
      res.addHeader("Set-Cookie", ResponseCookie.from(REFRESH_COOKIE, "")
          .domain("localhost").httpOnly(true).secure(secure).sameSite(sameSite).path(p).maxAge(0).build().toString());
    }
  }

  // 최신 1개만 재세팅
  public static void setRefreshCookie(HttpServletResponse res, String token, boolean secure, String sameSite) {
    res.addHeader("Set-Cookie", ResponseCookie.from(REFRESH_COOKIE, token)
        .httpOnly(true).secure(secure).sameSite(sameSite).path("/")
        .maxAge(Duration.ofDays(14)).build().toString());
  }
}
