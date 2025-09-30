package com.example.blog.domain.post.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.PostgresContainerTest;
import com.example.QuerydslTestConfig;
import com.example.TestEntityFactory;
import com.example.blog.common.pagenation.Direction;
import com.example.blog.common.pagenation.PostSortBy;
import com.example.blog.domain.blog.entity.Blog;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.post.dto.PostPaginationRequest;
import com.example.blog.domain.post.dto.PostWithStat;
import com.example.blog.domain.post.entity.Post;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@DataJpaTest
@Import(QuerydslTestConfig.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostQueryRepositoryTest extends PostgresContainerTest {

    @Autowired
    private PostQueryRepositoryImpl postQueryRepository;

    @PersistenceContext
    private EntityManager em;

    private Member member;
    private Blog blog;

    @BeforeEach
    void setUp() {
        member = TestEntityFactory.member(em, "member1");
        blog = TestEntityFactory.blog(em, member, "blog1", "title", "desc");

        // Create 3 posts with different stats
        TestEntityFactory.seedPostBundle(em, member, blog, "post1", 10L, 5L, Instant.now().minus(2, ChronoUnit.DAYS));
        TestEntityFactory.seedPostBundle(em, member, blog, "post2", 20L, 10L, Instant.now().minus(1, ChronoUnit.DAYS));
        TestEntityFactory.seedPostBundle(em, member, blog, "post3", 30L, 15L, Instant.now());

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("게시물을 날짜 내림차순으로 정렬한다")
    void findByBlogIdAndQuery_SortByDate_Desc() {
        // given
        PostPaginationRequest request = new PostPaginationRequest(PostSortBy.DATE, Direction.DESC, 10L, null);

        // when
        List<PostWithStat> result = postQueryRepository.findByBlogIdAndQuery(blog.getId(), request);

        // then
        assertThat(result).hasSize(3);
        assertThat(result.stream().map(PostWithStat::post).map(Post::getTitle))
                .containsExactly("post3", "post2", "post1");
    }

    @Test
    @DisplayName("게시물을 좋아요 오름차순으로 정렬한다")
    void findByBlogIdAndQuery_SortByLikes_Asc() {
        // given
        PostPaginationRequest request = new PostPaginationRequest(PostSortBy.LIKES, Direction.ASC, 10L, null);

        // when
        List<PostWithStat> result = postQueryRepository.findByBlogIdAndQuery(blog.getId(), request);

        // then
        assertThat(result).hasSize(3);
        assertThat(result.stream().map(PostWithStat::post).map(Post::getTitle))
                .containsExactly("post1", "post2", "post3");
    }

    @Test
    @DisplayName("게시물을 조회수 내림차순으로 정렬한다")
    void findByBlogIdAndQuery_SortByViews_Desc() {
        // given
        PostPaginationRequest request = new PostPaginationRequest(PostSortBy.VIEWS, Direction.DESC, 10L, null);

        // when
        List<PostWithStat> result = postQueryRepository.findByBlogIdAndQuery(blog.getId(), request);

        // then
        assertThat(result).hasSize(3);
        assertThat(result.stream().map(PostWithStat::post).map(Post::getTitle))
                .containsExactly("post3", "post2", "post1");
    }

    @Test
    @DisplayName("게시물 조회시 limit + 1 만큼 조회한다")
    void findByBlogIdAndQuery_LimitPlusOne() {
        // given
        PostPaginationRequest request = new PostPaginationRequest(PostSortBy.VIEWS, Direction.DESC, 2L, null);

        // when
        List<PostWithStat> result = postQueryRepository.findByBlogIdAndQuery(blog.getId(), request);

        // then
        assertThat(result).hasSize(3);
    }
}
