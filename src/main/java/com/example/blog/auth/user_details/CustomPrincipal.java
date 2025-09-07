package com.example.blog.auth.user_details;


import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public record CustomPrincipal(
    UUID id,
    String email,
    String role,
    String status // ACTIVE / DELETED
) {
  public Collection<? extends GrantedAuthority> authorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + role));
  }
}
