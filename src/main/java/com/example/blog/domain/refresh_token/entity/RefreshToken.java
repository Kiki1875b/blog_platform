package com.example.blog.domain.refresh_token.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class RefreshToken {

  @Id
  @Column(updatable = false, nullable = false, name = "member_id", columnDefinition = "UUID")
  private UUID memberId;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String token;

  public RefreshToken(UUID memberId, String token){
    this.memberId = memberId;
    this.token = token;
  }

  public void updateToken(String token){
    this.token = token;
  }

  @CreatedDate
  @Column(nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at")
  @LastModifiedDate
  private Instant updatedAt;

}
