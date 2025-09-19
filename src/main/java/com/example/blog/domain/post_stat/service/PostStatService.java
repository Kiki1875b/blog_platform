package com.example.blog.domain.post_stat.service;

import com.example.blog.domain.post.entity.Post;
import com.example.blog.domain.post_stat.entity.PostStat;
import com.example.blog.domain.post_stat.repository.PostStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostStatService {

  private final PostStatRepository postStatRepository;

  @Transactional
  public PostStat createPostStat(Post post){
    PostStat stat = new PostStat();
    stat.setPost(post);
    postStatRepository.save(stat);
    return stat;
  }
}
