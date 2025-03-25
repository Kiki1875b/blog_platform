package com.example.blog.auth.jwt;

import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final MemberRepository memberRepository;
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String token = request.getHeader("Authorization");
    if(token != null && token.startsWith("Bearer")){
      token = token.substring(7);
      try{
        Claims claims = JwtUtil.parseToken(token);
        Long userId = Long.parseLong(claims.getSubject());

        Optional<Member> optionalMember = memberRepository.findById(userId);
        if(optionalMember.isPresent()){
          Member member = optionalMember.get();
          UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(member, null,
              Collections.emptyList());

          SecurityContextHolder.getContext().setAuthentication(authentication);
        }

      }catch (Exception e){
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return;
      }
    }

    filterChain.doFilter(request, response);
  }
}
