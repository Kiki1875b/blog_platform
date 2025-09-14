package com.example.blog.domain.post.service;

import com.example.blog.common.markdown.MarkdownService;
import com.example.blog.domain.blog.entity.Blog;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.post.dto.CreatePostRequestDto;
import com.example.blog.domain.post.entity.Post;
import com.example.blog.domain.post.mapper.PostMapper;
import com.example.blog.domain.post.repository.PostRepositoryPort;
import com.example.blog.domain.tag.entity.Tag;
import java.util.HashSet;
import java.util.List;
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
    post.updateTags(new HashSet<>(tags));


    post.updateHtml(mdService.toHtml(request.content()));


    return postPort.save(post);
  }
}
