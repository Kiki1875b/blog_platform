package com.example.blog.common.logging;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LoggingFilter implements Filter {

  // 로깅을 제외할 경로들
  private static final List<String> EXCLUDED_PATHS = Arrays.asList(
      "/css", "/js", "/images", "/favicon.ico", "/actuator", "/health"
  );

  // Body 로깅을 하지 않을 Content-Type들
  private static final List<String> EXCLUDED_CONTENT_TYPES = Arrays.asList(
      "application/x-www-form-urlencoded", // 폼 데이터
      "multipart/form-data" // 파일 업로드
  );

  @Override
  public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain filterChain) throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    // 제외할 경로인지 확인
    if (shouldSkipLogging(httpRequest)) {
      filterChain.doFilter(request, response);
      return;
    }

    long startTime = System.currentTimeMillis();
    String traceId = generateTraceId();
    MDC.put("traceId", traceId);

    try {
      // Content-Type에 따라 래핑 여부 결정
      if (shouldWrapRequest(httpRequest)) {
        CachedBodyHttpServletRequest wrappedRequest =
            new CachedBodyHttpServletRequest(httpRequest);

        logRequest(wrappedRequest, traceId);
        filterChain.doFilter(wrappedRequest, httpResponse);
        logResponse(wrappedRequest, httpResponse,
            System.currentTimeMillis() - startTime, traceId);
      } else {
        // 폼 데이터는 래핑하지 않고 기본 정보만 로깅
        logBasicRequest(httpRequest, traceId);
        filterChain.doFilter(httpRequest, httpResponse);
        logBasicResponse(httpRequest, httpResponse,
            System.currentTimeMillis() - startTime, traceId);
      }

    } catch (Exception ex) {
      logError(httpRequest, httpResponse,
          System.currentTimeMillis() - startTime, traceId, ex);
      throw ex;
    } finally {
      MDC.clear();
    }
  }

  private boolean shouldSkipLogging(HttpServletRequest request) {
    String uri = request.getRequestURI();
    return EXCLUDED_PATHS.stream().anyMatch(uri::startsWith);
  }

  private boolean shouldWrapRequest(HttpServletRequest request) {
    String contentType = request.getContentType();
    if (contentType == null) {
      return true; // Content-Type이 없으면 래핑
    }

    return EXCLUDED_CONTENT_TYPES.stream()
        .noneMatch(excludedType -> contentType.toLowerCase().startsWith(excludedType));
  }

  private void logRequest(CachedBodyHttpServletRequest request, String traceId) throws IOException {
    String method = request.getMethod();
    String uri = request.getRequestURI();
    String queryString = request.getQueryString();
    String clientIp = getClientIp(request);
    String contentType = request.getContentType();

    log.info("[{}] >>> REQUEST: {} {} {} | IP: {} | Content-Type: {}",
        traceId, method, uri,
        queryString != null ? "?" + queryString : "",
        clientIp, contentType);

    // JSON 요청의 경우에만 body 로깅
    if (("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method))
        && contentType != null && contentType.contains("json")) {

      String body = new String(request.getCachedBody(), StandardCharsets.UTF_8);
      if (!body.isEmpty()) {
        String maskedBody = maskSensitiveData(body);
        if (maskedBody.length() < 1000) {
          log.info("[{}] Request Body: {}", traceId, maskedBody);
        } else {
          log.info("[{}] Request Body: {}... (truncated, length: {})",
              traceId, maskedBody.substring(0, 500), body.length());
        }
      }
    }
  }

  private void logBasicRequest(HttpServletRequest request, String traceId) {
    String method = request.getMethod();
    String uri = request.getRequestURI();
    String queryString = request.getQueryString();
    String clientIp = getClientIp(request);
    String contentType = request.getContentType();

    log.info("[{}] >>> REQUEST: {} {} {} | IP: {} | Content-Type: {} | (Body not cached)",
        traceId, method, uri,
        queryString != null ? "?" + queryString : "",
        clientIp, contentType);

    // 폼 데이터의 경우 파라미터 이름들만 로깅 (값은 제외)
    if ("POST".equalsIgnoreCase(method) &&
        contentType != null && contentType.startsWith("application/x-www-form-urlencoded")) {

      List<String> paramNames = Collections.list(request.getParameterNames());
      if (!paramNames.isEmpty()) {
        log.info("[{}] Form Parameters: {}", traceId, paramNames);
      }
    }
  }

  private void logResponse(CachedBodyHttpServletRequest request,
      HttpServletResponse response, long duration, String traceId) {
    String method = request.getMethod();
    String uri = request.getRequestURI();
    int status = response.getStatus();
    String authStatus = getAuthenticationStatus();

    log.info("[{}] <<< RESPONSE: {} {} | Status: {} | Duration: {}ms | Auth: {}",
        traceId, method, uri, status, duration, authStatus);
  }

  private void logBasicResponse(HttpServletRequest request,
      HttpServletResponse response, long duration, String traceId) {
    String method = request.getMethod();
    String uri = request.getRequestURI();
    int status = response.getStatus();
    String authStatus = getAuthenticationStatus();

    log.info("[{}] <<< RESPONSE: {} {} | Status: {} | Duration: {}ms | Auth: {}",
        traceId, method, uri, status, duration, authStatus);
  }

  private void logError(HttpServletRequest request,
      HttpServletResponse response, long duration,
      String traceId, Exception ex) {
    String method = request.getMethod();
    String uri = request.getRequestURI();
    int status = response.getStatus();

    log.error("[{}] <<< ERROR: {} {} | Status: {} | Duration: {}ms | Error: {} | Message: {}",
        traceId, method, uri, status, duration,
        ex.getClass().getSimpleName(), ex.getMessage());
  }

  private String getAuthenticationStatus() {
    try {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if (auth != null && auth.isAuthenticated() &&
          !"anonymousUser".equals(auth.getPrincipal())) {
        return "authenticated(" + auth.getName() + ")";
      }
      return "anonymous";
    } catch (Exception e) {
      return "unknown";
    }
  }

  private String generateTraceId() {
    return UUID.randomUUID().toString().substring(0, 8);
  }

  private String getClientIp(HttpServletRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
      return xForwardedFor.split(",")[0].trim();
    }

    String xRealIp = request.getHeader("X-Real-IP");
    if (xRealIp != null && !xRealIp.isEmpty()) {
      return xRealIp;
    }

    return request.getRemoteAddr();
  }

  private String maskSensitiveData(String body) {
    if (body == null || body.isEmpty()) {
      return body;
    }

    return body
        .replaceAll("(\"password\"\\s*:\\s*\")[^\"]*\"", "$1***\"")
        .replaceAll("(\"token\"\\s*:\\s*\")[^\"]*\"", "$1***\"")
        .replaceAll("(\"secret\"\\s*:\\s*\")[^\"]*\"", "$1***\"")
        .replaceAll("(\"key\"\\s*:\\s*\")[^\"]*\"", "$1***\"")
        .replaceAll("(\"ssn\"\\s*:\\s*\")[^\"]*\"", "$1***\"")
        .replaceAll("(\"email\"\\s*:\\s*\")[^\"]*\"", "$1***@***\""); // 이메일도 마스킹
  }
}
