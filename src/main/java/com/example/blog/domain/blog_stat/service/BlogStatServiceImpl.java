package com.example.blog.domain.blog_stat.service;

import com.example.blog.domain.blog.entity.Blog;
import com.example.blog.domain.blog_stat.entity.BlogStat;
import com.example.blog.domain.blog_stat.repository.BlogStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class BlogStatServiceImpl implements BlogStatService{

  private final BlogStatRepository blogStatRepository;
  @Override
  @Transactional
  public BlogStat createBlogStat(Blog blog) {

    BlogStat stat = new BlogStat();
    stat.setBlog(blog);
    blogStatRepository.save(stat);

    return stat;
  }
}
