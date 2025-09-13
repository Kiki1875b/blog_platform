package com.example.blog.domain.blog.entity;

import com.example.blog.domain.base.BaseUpdatableEntity;
import com.example.blog.domain.blog_tag.entity.BlogTag;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.tag.entity.Tag;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity
@Table(name = "blogs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class Blog extends BaseUpdatableEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String description;

  @Enumerated(value = EnumType.STRING)
  private BlogVisibility visibility = BlogVisibility.PUBLIC;

  @Column(unique = true, nullable = false)
  private String slug;

  @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
  Set<BlogTag> blogTags = new HashSet<>();

  public void updateTitle(String title){
    if(title == null || title.isEmpty()) return;
    this.title = title;
  }

  public void updateDescription(String description){
    if(description == null || description.isEmpty()) return;
    this.description = description;
  }

  public void updateVisibility(BlogVisibility visibility){
    if(visibility == null) return;
    this.visibility = visibility;
  }

  public void updateTags(List<Tag> tags){
    if (this.blogTags == null) {
      this.blogTags = new HashSet<>();
    }

    if(tags.isEmpty()) return;
    tags.forEach(tag -> this.blogTags.add(new BlogTag(this, tag)));
  }

  public void updateTags(Set<Tag> newTags) {
    // 새로 들어온 테그 목록에 없는 기존 테그 삭제
    blogTags.removeIf(bt -> !newTags.contains(bt.getTag()));

    // 새로운 테그 삽입
    newTags.forEach(tag -> this.blogTags.add(new BlogTag(this, tag)));
  }

  public List<String> getTagNames() {
    return blogTags.stream()
        .map(bt -> bt.getTag().getName())
        .toList();
  }
}
