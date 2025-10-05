package com.example.blog.domain.post.repository;

import com.example.blog.common.exception.ErrorCode;
import com.example.blog.common.exception.PostException;
import com.example.blog.domain.post.dto.PostPaginationRequest;
import com.example.blog.domain.post.dto.PostWithStat;
import com.example.blog.domain.post.entity.Post;
import java.util.List;
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
  public Post findByIdJoinTag(UUID id) {
    return postRepository.findByIdWithTags(id).orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));
  }


  @Override
  public Post save(Post post) {
    return postRepository.save(post);
  }

  @Override
  public List<PostWithStat> findByBlogIdAndQuery(UUID id, PostPaginationRequest query) {
    return postRepository.findByBlogIdAndQuery(id, query);
  }
}
