package com.example.blog.domain.post_tag.entity;

import com.example.blog.domain.base.BaseEntity;
import com.example.blog.domain.post.entity.Post;
import com.example.blog.domain.tag.entity.Tag;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name = "post_tags")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PostTag extends BaseEntity {



  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tag_id", nullable = false)
  private Tag tag;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PostTag)) return false;
    PostTag that = (PostTag) o;
    return Objects.equals(post.getId(), that.post.getId()) &&
        Objects.equals(tag.getId(), that.tag.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(post.getId(), tag.getId());
  }
}
