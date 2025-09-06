package com.example.blog.domain.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PreUpdate;
import java.time.Instant;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

@MappedSuperclass
@Getter
public class BaseUpdatableEntity extends BaseEntity{
  @LastModifiedDate
  @Column(name = "updated_at")
  private Instant updatedAt;
}
