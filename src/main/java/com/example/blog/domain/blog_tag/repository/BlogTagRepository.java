package com.example.blog.domain.blog_tag.repository;

import com.example.blog.domain.blog_tag.entity.BlogTag;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BlogTagRepository extends JpaRepository<BlogTag, UUID> {

  @Query("""
        SELECT bt
        FROM blog_tags bt
        JOIN FETCH bt.tag t
        WHERE bt.blog.id IN :blogIds
  """)
  List<BlogTag> findAllByBlogId(List<UUID> blogIds);
}
