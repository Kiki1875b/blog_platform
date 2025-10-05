package com.example.blog.domain.post.service;

import com.example.blog.common.exception.ErrorCode;
import com.example.blog.common.exception.PostException;
import com.example.blog.common.markdown.MarkdownService;
import com.example.blog.domain.blog.entity.Blog;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.post.dto.CreatePostRequestDto;
import com.example.blog.domain.post.dto.UpdatePostRequestDto;
import com.example.blog.domain.post.entity.Post;
import com.example.blog.domain.post.mapper.PostMapper;
import com.example.blog.domain.post.repository.PostRepositoryPort;
import com.example.blog.domain.tag.entity.Tag;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostCommandServiceImpl implements PostCommandService{


  private final MarkdownService mdService;
  private final PostMapper postMapper;
  private final PostRepositoryPort postPort;

  @Override
  @Transactional
  public Post createPost(Member member, Blog blog, List<Tag> tags, CreatePostRequestDto request) {

    Post post = postMapper.toEntity(request);
    post.addAuthor(member);
    post.addBlog(blog);
    HashSet<Tag> tags1 = new HashSet<>(tags);

    post.updateTags(tags1);

    post.updateHtml(mdService.toHtml(request.content()));


    return postPort.save(post);
  }

  @Override
  @Transactional
  public void updatePost(UUID postId, Member member, List<Tag> tags, UpdatePostRequestDto request) {
    Post post = postPort.findById(postId);

    if (!post.getMember().getId().equals(member.getId())) {
        throw new PostException(ErrorCode.WRONG_BLOG_OWNER);
    }

    String html = null;
    if (request.getContent() != null) {
        html = mdService.toHtml(request.getContent());
    }

    post.update(request.getTitle(), request.getContent(), request.getStatus(), html);

    if (tags != null) {
        post.updateTags(new HashSet<>(tags));
    }

    postPort.save(post);
  }
}
