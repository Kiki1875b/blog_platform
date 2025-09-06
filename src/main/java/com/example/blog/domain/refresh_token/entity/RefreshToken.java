package com.example.blog.domain.refresh_token.entity;

import com.example.blog.domain.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

  @Id
  @Column(updatable = false, nullable = false, name = "member_id")
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

  @CreatedDate
  @Column(nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at")
  private Instant updatedAt;

  @PreUpdate
  private void onUpdate(){
    this.updatedAt = Instant.now();
  }
}
