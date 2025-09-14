package com.example.blog.domain.blog_stat.service;

import com.example.blog.domain.blog.entity.Blog;
import com.example.blog.domain.blog_stat.entity.BlogStat;

public interface BlogStatService {
  BlogStat createBlogStat(Blog blog);
}
