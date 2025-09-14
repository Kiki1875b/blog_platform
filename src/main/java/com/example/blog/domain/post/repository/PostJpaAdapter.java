package com.example.blog.domain.post.repository;

import com.example.blog.common.exception.ErrorCode;
import com.example.blog.common.exception.PostException;
import com.example.blog.domain.post.entity.Post;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostJpaAdapter implements PostRepositoryPort{

  private final PostRepository postRepository;

  @Override
  public Post findById(UUID uuid) {
    return postRepository.findById(uuid).orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));
  }

  @Override
  public Post save(Post post) {
    return postRepository.save(post);
  }
}
