package com.example;

import com.example.blog.common.enumerated.MemberStatus;
import com.example.blog.domain.blog.entity.Blog;
import com.example.blog.domain.blog.entity.BlogVisibility;
import com.example.blog.domain.blog_stat.entity.BlogStat;
import com.example.blog.domain.blog_tag.entity.BlogTag;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.entity.MemberRole;
import com.example.blog.domain.post.entity.Post;
import com.example.blog.domain.post.entity.PostState;
import com.example.blog.domain.post_stat.entity.PostStat;
import com.example.blog.domain.tag.entity.Tag;
import com.example.blog.domain.comment.entity.Comment;
import jakarta.persistence.EntityManager;

import java.time.Instant;
import java.util.HashSet;
import java.util.UUID;
import org.springframework.test.util.ReflectionTestUtils;

public class TestEntityFactory {

  public static Member createMember(){
    Member ret = new Member("test", "pwd", "nick", null, null, MemberStatus.ACTIVE, null, "name", MemberRole.USER);
    ReflectionTestUtils.setField(ret, "id", UUID.randomUUID());
    return ret;
  }

  public static Member member(EntityManager em, String emailSuffix) {
    Member m = new Member(
        "test" + emailSuffix + "@test.com",
        "pwd",
        "nickname",
        null, 
        null, 
        MemberStatus.ACTIVE, 
        null,
        "tester",
        MemberRole.USER
    );
    em.persist(m);
    return m;
  }

  public static Post createPost(Member member, Blog blog){
    Post p = new Post(
        blog, member, "title", "content", null, PostState.PUBLIC, new HashSet<>()
    );
    ReflectionTestUtils.setField(p, "id", UUID.randomUUID());

    return p;
  }
  public static Tag createTag(String tagName){
    Tag t = new Tag(tagName);
    ReflectionTestUtils.setField(t, "id", UUID.randomUUID());
    return t;
  }
  public static Blog createBlog(Member member){
    Blog b = new Blog(member, "title", "description", BlogVisibility.PUBLIC, "slug", new HashSet<>());
    ReflectionTestUtils.setField(b, "id", UUID.randomUUID());
    return b;
  }

  public static Blog blog(EntityManager em, Member owner, String slug, String title, String desc) {
    Blog b = new Blog(owner, title, desc, BlogVisibility.PUBLIC, slug, new HashSet<>());
    em.persist(b);
    return b;
  }

  public static Tag tag(EntityManager em, String name) {
    Tag t = new Tag(name);
    em.persist(t);
    return t;
  }

  public static BlogTag link(EntityManager em, Blog blog, Tag tag) {
    BlogTag bt = new BlogTag(blog, tag);
    em.persist(bt);
    return bt;
  }

  public static BlogStat stat(EntityManager em, Blog blog, long posts, long views, long followers) {
    BlogStat s = new BlogStat(
        blog.getId(),
        blog,
        posts,
        views,
        followers,
        null,
        null
    );
    em.persist(s);
    return s;
  }

  /**
   * i 값에 비례하는 메트릭을 넣어 일관적인 정렬 검증을 가능하게 함.
   * posts = i*5, views = i*10, followers = i*2
   */
  public static Blog seedBlogBundle(EntityManager em, Member owner, int i) {
    String slug = "slug-" + i;
    Blog blog = blog(em, owner, slug, "title-" + i, "desc-" + i);

    stat(em, blog, i * 5L, i * 10L, i * 2L);
    Tag tag = tag(em, "tag-" + i);
    link(em, blog, tag);

    return blog;
  }

  public static Post seedPostBundle(EntityManager em, Member owner, Blog blog, String title, long views, long likes, Instant createdAt) {
    Post post = new Post(blog, owner, title, "content", "contentHtml", PostState.PUBLIC, new HashSet<>());
    ReflectionTestUtils.setField(post, "createdAt", createdAt);
    em.persist(post);

    PostStat stat = new PostStat();
    ReflectionTestUtils.setField(stat, "post", post);
    ReflectionTestUtils.setField(stat, "id", post.getId());
    ReflectionTestUtils.setField(stat, "viewCount", views);
    ReflectionTestUtils.setField(stat, "likeCount", likes);
    em.persist(stat);

    return post;
  }


}