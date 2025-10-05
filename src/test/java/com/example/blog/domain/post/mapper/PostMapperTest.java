package com.example.blog.domain.post.mapper;

import com.example.TestEntityFactory;
import com.example.blog.domain.blog.entity.Blog;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.post.dto.PostResponseDto;
import com.example.blog.domain.post.entity.Post;
import com.example.blog.domain.post_stat.entity.PostStat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class PostMapperTest {

    private PostMapper postMapper;

    @BeforeEach
    void setUp() {
        postMapper = new PostMapperImpl();
    }

    @Test
    @DisplayName("Post를 PostResponseDto로 매핑할 수 있다")
    void postToPostResponseDto() {
        // given
        Member member = TestEntityFactory.createMember();
        Blog blog = TestEntityFactory.createBlog(member);
        Post post = TestEntityFactory.createPost(member, blog);
        ReflectionTestUtils.setField(post, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(post, "createdAt", Instant.now());


        // when
        PostResponseDto responseDto = postMapper.toResponse(post);

        // then
        assertThat(responseDto.postId()).isEqualTo(post.getId());
        assertThat(responseDto.blogId()).isEqualTo(blog.getId());
        assertThat(responseDto.authorId()).isEqualTo(member.getId());
        assertThat(responseDto.title()).isEqualTo(post.getTitle());
        assertThat(responseDto.content()).isEqualTo(post.getContent());
        assertThat(responseDto.contentHtml()).isEqualTo(post.getContentHtml());
        assertThat(responseDto.status()).isEqualTo(post.getState());
        assertThat(responseDto.tags()).isEqualTo(post.getTagNames());
        assertThat(responseDto.createdAt()).isEqualTo(post.getCreatedAt());
        assertThat(responseDto.views()).isEqualTo(0L);
        assertThat(responseDto.likes()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Post와 PostStat을 PostResponseDto로 매핑할 수 있다")
    void postAndPostStatToPostResponseDto() {
        // given
        Member member = TestEntityFactory.createMember();
        Blog blog = TestEntityFactory.createBlog(member);
        Post post = TestEntityFactory.createPost(member, blog);
        ReflectionTestUtils.setField(post, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(post, "createdAt", Instant.now());

        PostStat postStat = new PostStat();
        ReflectionTestUtils.setField(postStat, "viewCount", 100L);
        ReflectionTestUtils.setField(postStat, "likeCount", 10L);


        // when
        PostResponseDto responseDto = postMapper.toResponse(post, postStat);

        // then
        assertThat(responseDto.postId()).isEqualTo(post.getId());
        assertThat(responseDto.blogId()).isEqualTo(blog.getId());
        assertThat(responseDto.authorId()).isEqualTo(member.getId());
        assertThat(responseDto.title()).isEqualTo(post.getTitle());
        assertThat(responseDto.content()).isEqualTo(post.getContent());
        assertThat(responseDto.contentHtml()).isEqualTo(post.getContentHtml());
        assertThat(responseDto.status()).isEqualTo(post.getState());
        assertThat(responseDto.tags()).isEqualTo(post.getTagNames());
        assertThat(responseDto.createdAt()).isEqualTo(post.getCreatedAt());
        assertThat(responseDto.views()).isEqualTo(100L);
        assertThat(responseDto.likes()).isEqualTo(10L);
    }
}
