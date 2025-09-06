package com.example.blog.domain.blog.entity;

import com.example.blog.domain.base.BaseEntity;
import com.example.blog.domain.base.BaseUpdatableEntity;
import com.example.blog.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity(name = "blogs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class Blog extends BaseUpdatableEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;
  private String title;
  private String description;
  @Enumerated(value = EnumType.STRING)
  private BlogVisibility visibility = BlogVisibility.PUBLIC;

  @Column(unique = true)
  private String slug;
}
