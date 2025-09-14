package com.example.blog.domain.blog.dto;

import com.example.blog.domain.blog.entity.Blog;
import com.example.blog.domain.blog_stat.entity.BlogStat;

public record BlogWithStat(Blog blog, BlogStat stat) {

}
