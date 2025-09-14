package com.example.blog.domain.blog.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.PostgresContainerTest;
import com.example.QuerydslTestConfig;
import com.example.TestEntityFactory;
import com.example.blog.common.pagenation.Direction;
import com.example.blog.common.pagenation.SortBy;
import com.example.blog.domain.blog.dto.BlogPaginationRequest;
import com.example.blog.domain.blog.dto.BlogWithStat;
import com.example.blog.domain.blog.entity.Blog;
import com.example.blog.domain.blog.respository.BlogQueryRepositoryImpl;
import com.example.blog.domain.member.entity.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;


@DataJpaTest
@Import({QuerydslTestConfig.class})
@ActiveProfiles("test")
class BlogQueryRepositoryImplTest extends PostgresContainerTest {
  @Autowired
  private BlogQueryRepositoryImpl blogQueryRepository;
  @PersistenceContext
  private EntityManager em;

  private Member member;

  @BeforeEach
  void setUp() {
    member = TestEntityFactory.member(em, "1");
    IntStream.range(0, 3).forEach(i -> TestEntityFactory.seedBlogBundle(em, member, i));
    em.flush();
    em.clear();
  }

  @Test
  @DisplayName("viewCount DESC 정렬")
  void findByMemberIdAndQuery_viewCount_desc() {
    BlogPaginationRequest request = new BlogPaginationRequest(SortBy.VIEWS, Direction.DESC, 10, null);

    List<BlogWithStat> result = blogQueryRepository.findByMemberIdAndQuery(member.getId(), request);
    List<Blog> blogs = result.stream().map(BlogWithStat::blog).toList();
    assertThat(result).hasSize(3);
    assertThat(blogs).extracting(Blog::getSlug)
        .containsExactly("slug-2", "slug-1", "slug-0");
  }

  @Test
  @DisplayName("followerCount ASC 정렬")
  void findByMemberIdAndQuery_followerCount_asc() {
    BlogPaginationRequest request = new BlogPaginationRequest(SortBy.FOLLOWERS, Direction.ASC, 10, null);

    List<BlogWithStat> result = blogQueryRepository.findByMemberIdAndQuery(member.getId(), request);
    List<Blog> blogs = result.stream().map(BlogWithStat::blog).toList();

    assertThat(result).hasSize(3);
    assertThat(blogs).extracting(Blog::getSlug)
        .containsExactly("slug-0", "slug-1", "slug-2");
  }

  @Test
  @DisplayName("postCount DESC 정렬 + limit+1 적용 확인")
  void findByMemberIdAndQuery_postCount_desc_limitPlusOne() {
    BlogPaginationRequest request = new BlogPaginationRequest(SortBy.POSTS, Direction.DESC, 1, null);

    List<BlogWithStat> result = blogQueryRepository.findByMemberIdAndQuery(member.getId(), request);
    List<Blog> blogs = result.stream().map(BlogWithStat::blog).toList();

    assertThat(result).hasSize(2); // limit=2이지만 내부 fetch는 3 (= limit+1)
    assertThat(blogs).extracting(Blog::getSlug)
        .containsExactly("slug-2", "slug-1");
  }

}
