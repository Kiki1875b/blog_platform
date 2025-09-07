package com.example.blog.domain.blog_tag.entity;

import com.example.blog.domain.base.BaseEntity;
import com.example.blog.domain.blog.entity.Blog;
import com.example.blog.domain.tag.entity.Tag;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity(name = "blog_tags")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BlogTag extends BaseEntity{

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "blog_id", nullable = false)
  private Blog blog;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "tag_id", nullable = false)
  private Tag tag;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof BlogTag)) return false;
    BlogTag that = (BlogTag) o;
    return Objects.equals(blog.getId(), that.blog.getId()) &&
        Objects.equals(tag.getId(), that.tag.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(blog.getId(), tag.getId());
  }
}
