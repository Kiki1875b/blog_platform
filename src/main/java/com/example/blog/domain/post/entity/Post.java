package com.example.blog.domain.post.entity;

import com.example.blog.domain.base.BaseUpdatableEntity;
import com.example.blog.domain.blog.entity.Blog;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.post_tag.entity.PostTag;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name = "posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Post extends BaseUpdatableEntity {

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "blog_id")
  private Blog blog;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String content;

  private String contentHtml;

  @Enumerated(EnumType.STRING)
  private PostState state;

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  Set<PostTag> postTags = new HashSet<>();


  public void addAuthor(Member member){
    if (member == null) return;
    this.member = member;
  }

  public void addBlog(Blog blog){
    if(blog == null) return;
    this.blog = blog;
  }
  public void updateTags(Set<Tag> tags){
    if(this.postTags == null) postTags = new HashSet<>();
    postTags.removeIf(pt -> !tags.contains(pt.getTag()));
    tags.forEach(t -> this.postTags.add(new PostTag(this, t)));
  }

  public void updateTitle(String title){
    if(title == null || title.isEmpty()) return;
    this.title = title;
  }

  public void updateContent(String content){
    if(content == null || content.isEmpty()) return;
    this.content = content;
  }

  public void updateHtml(String contentHtml){
    if(contentHtml == null || contentHtml.isEmpty()) return;
    this.contentHtml = contentHtml; // TODO: 저장안될시 후처리 필요
  }

  public List<String> getTagNames(){
    return postTags.stream()
        .map(pt -> pt.getTag().getName())
        .toList();
  }
}
