package com.example.blog.domain.blog_stat.repository;

import com.example.blog.domain.blog_stat.entity.BlogStat;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogStatRepository extends JpaRepository<BlogStat, UUID> {

}
