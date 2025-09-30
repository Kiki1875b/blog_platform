package com.example.blog.domain.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.example.TestEntityFactory;
import com.example.blog.common.pagenation.Direction;
import com.example.blog.common.pagenation.PaginatedResponse;
import com.example.blog.common.pagenation.PostSortBy;
import com.example.blog.domain.blog.entity.Blog;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.post.dto.PostPaginationRequest;
import com.example.blog.domain.post.dto.PostResponseDto;
import com.example.blog.domain.post.dto.PostWithStat;
import com.example.blog.domain.post.entity.Post;
import com.example.blog.domain.post.mapper.PostMapper;
import com.example.blog.domain.post.repository.PostRepositoryPort;
import com.example.blog.domain.post_stat.entity.PostStat;
import com.example.blog.domain.post_tag.repository.PostTagRepository;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PostQueryServiceTest {

    @InjectMocks
    private PostQueryService postQueryService;

    @Mock
    private PostRepositoryPort postRepositoryPort;

    @Mock
    private PostTagRepository postTagRepository;

    @Mock
    private PostMapper postMapper;

    private Member member;
    private Blog blog;

    @BeforeEach
    void setUp() {
        member = TestEntityFactory.createMember();
        blog = TestEntityFactory.createBlog(member);
    }

    @Test
    @DisplayName("블로그 게시물 목록을 페이지네이션하여 조회한다")
    void getBlogPosts() {
        // given
        PostPaginationRequest request = new PostPaginationRequest(PostSortBy.DATE, Direction.DESC, 10L
            , null);

        Post post = TestEntityFactory.createPost(member, blog);
        ReflectionTestUtils.setField(post, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(post, "createdAt", Instant.now());

        PostStat stat = new PostStat();
        ReflectionTestUtils.setField(stat, "viewCount", 100L);
        ReflectionTestUtils.setField(stat, "likeCount", 10L);

        PostWithStat postWithStat = new PostWithStat(post, stat);
        List<PostWithStat> postWithStats = List.of(postWithStat);

        PostResponseDto responseDto = new PostResponseDto(
                post.getId(), blog.getId(), member.getId(), post.getTitle(), post.getContent(),
                post.getContentHtml(), post.getState(), post.getTagNames(), post.getCreatedAt(),
                stat.getViewCount(), stat.getLikeCount()
        );
        List<PostResponseDto> dtoList = List.of(responseDto);

        given(postRepositoryPort.findByBlogIdAndQuery(blog.getId(), request)).willReturn(postWithStats);
        given(postTagRepository.findAllByPost_IdIn(any())).willReturn(Collections.emptyList());
        given(postMapper.toPostListResponse(postWithStats)).willReturn(dtoList);

        // when
        PaginatedResponse<PostResponseDto> result = postQueryService.getBlogPosts(blog, request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.items()).hasSize(1);
        assertThat(result.items().get(0).postId()).isEqualTo(post.getId());
        assertThat(result.pageInfo().hasNext()).isFalse();

        then(postRepositoryPort).should().findByBlogIdAndQuery(blog.getId(), request);
        then(postTagRepository).should().findAllByPost_IdIn(List.of(post.getId()));
        then(postMapper).should().toPostListResponse(postWithStats);
    }
}
