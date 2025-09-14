package com.example.blog.domain.post_stat.entity;

import com.example.blog.domain.post.entity.Post;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "post_stats")
public class PostStat {
  @Id
  @Column(columnDefinition = "UUID")
  private UUID id;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "id")
  private Post post;

  @Column(name = "view_count", nullable = false)
  private long viewCount = 0L;

  @Column(name = "like_count", nullable = false)
  private long likeCount = 0L;

  @CreatedDate
  @Column(nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at")
  private Instant updatedAt;

  @PreUpdate
  private void onUpdate(){
    this.updatedAt = Instant.now();
  }

  public void setPost(Post post){
    if(post == null) return;
    this.post = post;
  }
}
