package com.example.blog.auth.service;

import com.example.blog.domain.member.entity.Member;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

public interface PrincipalMember {
  Member getMember();
  Collection<? extends GrantedAuthority> getAuthorities();
  boolean isOAuthUser();
}
