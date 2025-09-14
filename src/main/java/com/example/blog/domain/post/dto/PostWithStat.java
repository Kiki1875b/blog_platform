package com.example.blog.domain.post.dto;

import com.example.blog.domain.post.entity.Post;
import com.example.blog.domain.post_stat.entity.PostStat;

public record PostWithStat(Post post, PostStat stat) {

}
