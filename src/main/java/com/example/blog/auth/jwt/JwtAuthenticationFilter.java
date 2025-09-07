package com.example.blog.auth.jwt;

import com.example.blog.auth.user_details.CustomPrincipal;
import com.example.blog.auth.user_details.CustomUserDetails;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final MemberRepository memberRepository;
  private final JwtService jwtService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      try {
        Claims claims = jwtService.parse(token);
        UUID userId = UUID.fromString(claims.getSubject());

        String email = claims.get("email", String.class);
        String role = claims.get("role", String.class);
        String name = claims.get("name", String.class);
        String status = claims.get("status", String.class);

        if (!"ACTIVE".equals(status)) {
          unauthorized(response, "inactive_account");
          return;
        }

        CustomPrincipal principal = new CustomPrincipal(userId, email, role, status);

        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(principal, null, principal.authorities());
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(auth);
      } catch (Exception e) {
        SecurityContextHolder.clearContext();
        unauthorized(response, "invalid_token");
        return;
      }
    }

    filterChain.doFilter(request, response);
  }

  private void unauthorized(HttpServletResponse response, String reason) throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");
    response.getWriter().write("{\"error\": \"" + reason + "\"}");
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getServletPath();
    return path.startsWith("/api/auth/") || path.startsWith("/api/public/") || path.equals("/api/token/refresh");
  }
}
