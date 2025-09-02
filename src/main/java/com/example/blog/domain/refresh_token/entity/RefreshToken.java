package com.example.blog.domain.refresh_token.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

  @Id
  @Column(updatable = false, nullable = false)
  private UUID memberId;

  @Column(nullable = false)
  private String token;

  public RefreshToken(UUID memberId, String token){
    this.memberId = memberId;
    this.token = token;
  }

  public void updateToken(String token){
    this.token = token;
  }
}
