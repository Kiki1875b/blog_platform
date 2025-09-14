package com.example.blog.common.config;

import com.example.blog.auth.handler.CustomOAuth2AuthenticationSuccessHandler;
import com.example.blog.auth.handler.FormLoginSuccessHandler;
import com.example.blog.auth.jwt.JwtAuthenticationFilter;
import com.example.blog.auth.jwt.JwtService;
import com.example.blog.auth.oauth.CustomOAuth2UserService;
import com.example.blog.auth.user_details.CustomUserDetailsService;
import com.example.blog.domain.member.repository.MemberRepository;
import com.example.blog.domain.refresh_token.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {


  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final RefreshTokenRepository refreshTokenRepository;
  private final MemberRepository memberRepository;
  private final CustomOAuth2UserService customOAuth2UserService;
  private final FormLoginSuccessHandler formLoginSuccessHandler;
  private final CustomUserDetailsService customUserDetailsService;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtService jwtService) throws Exception{
    http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/favicon.ico",
                "/api/oauth2/**",
                "/api/public/**",
                "/api/token/refresh",
                "/oauth2/**",
                "/api/auth/**",
                "/api/member/{memberId}/blogs"
            )
            .permitAll()
            .requestMatchers("/favicon.ico").permitAll()
            .requestMatchers("/api/private/**").authenticated()
            .requestMatchers("/api/posts/**").hasRole("USER")
            .requestMatchers("/api/member/**").hasRole("USER")
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginProcessingUrl("/api/auth/login")
            .usernameParameter("email")
            .passwordParameter("password")
            .successHandler(formLoginSuccessHandler)
        )
        .oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
            .successHandler(new CustomOAuth2AuthenticationSuccessHandler(memberRepository,  jwtService, refreshTokenRepository ))
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

  @Bean
  public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource(){
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:3000"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);
    config.setExposedHeaders(List.of("Authorization", "Refresh-Token"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }

  @Bean
  public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
    builder.userDetailsService(customUserDetailsService)
        .passwordEncoder(passwordEncoder());
    return builder.build();
  }

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> web.ignoring().requestMatchers(
        "/favicon.ico",
        "/css/**",
        "/js/**",
        "/images/**"
    );
  }
}
