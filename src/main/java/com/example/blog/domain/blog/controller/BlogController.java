package com.example.blog.domain.blog.controller;


import com.example.blog.auth.user_details.CustomPrincipal;
import com.example.blog.common.pagenation.PaginatedResponse;
import com.example.blog.domain.blog.dto.BlogPaginationRequest;
import com.example.blog.domain.blog.dto.BlogResponseDto;
import com.example.blog.domain.blog.dto.CreateBlogRequestDto;
import com.example.blog.domain.blog.dto.UpdateBlogRequestDto;
import com.example.blog.domain.blog.facade.BlogFacade;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BlogController {


  private final BlogFacade blogFacade;

  @PostMapping("/blogs")
  public ResponseEntity<BlogResponseDto> createBlog(@RequestBody CreateBlogRequestDto blogRequest, @AuthenticationPrincipal
      CustomPrincipal principal){

    BlogResponseDto responseDto = blogFacade.createBlog(blogRequest, principal);

    return ResponseEntity.ok(responseDto);
  }

  @GetMapping("/member/{memberId}/blogs")
  public ResponseEntity<PaginatedResponse<BlogResponseDto>> getMemberBlogs(
      @PathVariable UUID memberId,
      @ModelAttribute BlogPaginationRequest blogRequest
  ){

    PaginatedResponse<BlogResponseDto> res = blogFacade.getMemberBlogs(memberId, blogRequest);

    return ResponseEntity.ok(res);
  }

  @PatchMapping("/blogs/{blogId}")
  public ResponseEntity<BlogResponseDto> updateBlog(@PathVariable UUID blogId, @AuthenticationPrincipal CustomPrincipal principal, @RequestBody
      UpdateBlogRequestDto updateDto){

  }
}
