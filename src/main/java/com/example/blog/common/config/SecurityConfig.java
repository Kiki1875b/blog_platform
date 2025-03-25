package com.example.blog.common.config;

import com.example.blog.auth.handler.CustomOAuth2AuthenticationSuccessHandler;
import com.example.blog.auth.jwt.JwtAuthenticationFilter;
import com.example.blog.auth.service.CustomOAuth2UserService;
import com.example.blog.domain.member.repository.MemberRepository;
import com.example.blog.domain.refresh_token.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {


  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final RefreshTokenRepository refreshTokenRepository;
  private final MemberRepository memberRepository;
  private final CustomOAuth2UserService customOAuth2UserService;
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/oauth2/**", "/api/public/**", "/api/token/refresh", "/api/auth/**").permitAll()
            .requestMatchers("/api/private/**").authenticated()
            .anyRequest().denyAll()
        )
        .oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
            .successHandler(new CustomOAuth2AuthenticationSuccessHandler(memberRepository, refreshTokenRepository))
        )
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(((request, response, authException) -> {
              response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              response.setContentType("application/json");
              response.getWriter().write("{\"message\": \"unauthorized.\"}");
            })))
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
