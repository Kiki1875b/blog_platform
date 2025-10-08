package com.example.blog.domain.post.service;

import com.example.blog.common.pagenation.PageInfo;
import com.example.blog.common.pagenation.PaginatedResponse;
import com.example.blog.common.pagenation.PaginationUtil;
import com.example.blog.domain.blog.entity.Blog;
import com.example.blog.domain.post.dto.PostPaginationRequest;
import com.example.blog.domain.post.dto.PostResponseDto;
import com.example.blog.domain.post.dto.PostWithStat;
import com.example.blog.domain.post.entity.Post;
import com.example.blog.domain.post.mapper.PostMapper;
import com.example.blog.domain.post.repository.PostRepositoryPort;
import com.example.blog.domain.post_tag.entity.PostTag;
import com.example.blog.domain.post_tag.repository.PostTagRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostQueryService {

  private final PostRepositoryPort postRepositoryPort;
  private final PostTagRepository postTagRepository;
  private final PostMapper postMapper;

  public Post getPostById(UUID uuid) {
    return postRepositoryPort.findById(uuid);
  }

  public Post getPostByIdWithTag(UUID uuid) {
    return postRepositoryPort.findByIdJoinTag(uuid);
  }

  public PaginatedResponse<PostResponseDto> getBlogPosts(Blog blog, PostPaginationRequest request) {
    List<PostWithStat> posts = postRepositoryPort.findByBlogIdAndQuery(blog.getId(), request);
    List<UUID> postIds = extractPostIds(posts);
    List<PostTag> postTags = postTagRepository.findAllByPost_IdIn(postIds);
    PageInfo pageInfo = PaginationUtil.createPageForPost(posts, request.limit(), request.postSortBy());
    List<PostResponseDto> responseDtoList = postMapper.toPostListResponse(posts);

    return new PaginatedResponse<>(responseDtoList, pageInfo);
  }

  private List<UUID> extractPostIds(List<PostWithStat> postWithStats){
    return postWithStats.stream()
        .map(PostWithStat::post)
        .map(Post::getId)
        .toList();
  }
}
