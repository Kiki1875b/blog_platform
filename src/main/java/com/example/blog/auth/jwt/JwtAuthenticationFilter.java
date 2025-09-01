package com.example.blog.auth.jwt;

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
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

//@Component
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//  private final MemberRepository memberRepository;
//  private final JwtService jwtService;
//  @Override
//  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
//      FilterChain filterChain) throws ServletException, IOException {
//    String token = extractTokenFromCookie(request, "ACCESS_TOKEN");
//    if (token != null) {
//      try {
//        Claims claims = jwtService.parseToken(token);
//        Long userId = Long.parseLong(claims.getSubject());
//
//        memberRepository.findById(userId).ifPresent(member -> {
//          CustomUserDetails userDetails = new CustomUserDetails(member);
//          UsernamePasswordAuthenticationToken auth =
//              new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//          SecurityContextHolder.getContext().setAuthentication(auth);
//        });
//      } catch (Exception e) {
//        SecurityContextHolder.clearContext();
//      }
//    }
//    filterChain.doFilter(request, response);
//  }
//
//  private String extractTokenFromCookie(HttpServletRequest request, String name) {
//    if (request.getCookies() == null) return null;
//    return Arrays.stream(request.getCookies())
//        .filter(c -> name.equals(c.getName()))
//        .map(Cookie::getValue)
//        .findFirst().orElse(null);
//  }
//
//}

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
        Claims claims = jwtService.parseToken(token);
        UUID userId = UUID.fromString(claims.getSubject());

        memberRepository.findById(userId).ifPresent(member -> {
          CustomUserDetails userDetails = new CustomUserDetails(member);
          UsernamePasswordAuthenticationToken auth =
              new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
          SecurityContextHolder.getContext().setAuthentication(auth);
        });
      } catch (Exception e) {
        SecurityContextHolder.clearContext();
      }
    }

    filterChain.doFilter(request, response);
  }
}
