package com.example.blog.domain.post.facade;

import com.example.blog.auth.user_details.CustomPrincipal;
import com.example.blog.common.pagenation.BlogPostPaginatedResponse;
import com.example.blog.common.pagenation.PaginatedResponse;
import com.example.blog.domain.blog.entity.Blog;
import com.example.blog.domain.blog.service.BlogService;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.service.MemberService;
import com.example.blog.domain.post.dto.CreatePostRequestDto;
import com.example.blog.domain.post.dto.PostPaginationRequest;
import com.example.blog.domain.post.dto.PostResponseDto;
import com.example.blog.domain.post.entity.Post;
import com.example.blog.domain.post.mapper.PostMapper;
import com.example.blog.domain.post.service.PostCommandService;
import com.example.blog.domain.post.service.PostQueryService;
import com.example.blog.domain.post_stat.entity.PostStat;
import com.example.blog.domain.post_stat.service.PostStatService;
import com.example.blog.domain.tag.entity.Tag;
import com.example.blog.domain.tag.service.TagService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PostFacadeImpl implements PostFacade {

  private final PostQueryService postQueryService;
  private final PostCommandService postCommandService;
  private final BlogService blogService;
  private final MemberService memberService;
  private final TagService tagService;
  private final PostStatService postStatService;
  private final PostMapper postMapper;

  @Override
  @Transactional
  public PostResponseDto createPost(CustomPrincipal principal, CreatePostRequestDto request, UUID blogId) {

    Member author = memberService.findMemberProxy(principal);
    Blog blog = blogService.findById(blogId);
    List<Tag> tags = tagService.getOrCreateTags(request.tags());
    Post post = postCommandService.createPost(author, blog, tags, request);

    postStatService.createPostStat(post); // TODO : 이벤트 분리

    return postMapper.toResponse(post);
  }

  @Override
  public PostResponseDto getSinglePostById(UUID postId) {
    Post post = postQueryService.getPostByIdWithTag(postId);
    PostStat stat = postStatService.getPostStatById(post);
    return postMapper.toResponse(post);
  }

  @Override
  public BlogPostPaginatedResponse getBlogPosts(UUID blogId,
      PostPaginationRequest request) {
    Blog blog = blogService.findById(blogId);
    PaginatedResponse<PostResponseDto> response = postQueryService.getBlogPosts(blog, request);
    return new BlogPostPaginatedResponse(blogId, blog.getTitle(), blog.getDescription(), blog.getTagNames(), response); // TODO: Mapper 로 이전하기
  }
}
