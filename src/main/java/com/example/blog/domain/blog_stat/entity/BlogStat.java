package com.example.blog.domain.blog_stat.entity;

import com.example.blog.domain.base.BaseEntity;
import com.example.blog.domain.blog.entity.Blog;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PreUpdate;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BlogStat {

  @Id
  @Column(columnDefinition = "UUID")
  private UUID id;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "id")
  private Blog blog;

  @Column(name = "post_count", nullable = false)
  private long postCount = 0L;

  @Column(name = "view_count", nullable = false)
  private long viewCount = 0L;

  @Column(name = "follower_count", nullable = false)
  private long followerCount = 0L;

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
