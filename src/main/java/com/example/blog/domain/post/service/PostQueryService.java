package com.example.blog.domain.post.service;

import com.example.blog.domain.post.entity.Post;
import com.example.blog.domain.post.repository.PostRepositoryPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostQueryService {

  private final PostRepositoryPort postRepositoryPort;

  public Post getPostByIdWithTag(UUID uuid) {
    return postRepositoryPort.findByIdJoinTag(uuid);
  }
}
