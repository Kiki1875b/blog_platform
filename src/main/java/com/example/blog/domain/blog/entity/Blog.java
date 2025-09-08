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

  @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL)
  Set<BlogTag> blogTags = new HashSet<>();

  public void addTags(List<Tag> tags){
    if (this.blogTags == null) {
      this.blogTags = new HashSet<>();
    }
    tags.forEach(tag -> this.blogTags.add(new BlogTag(this, tag)));
  }
}
