package com.example.blog.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.TestEntityFactory;
import com.example.blog.common.pagenation.PageInfo;
import com.example.blog.common.pagenation.PaginationUtil;
import com.example.blog.common.pagenation.PaginationUtil.Decoded;
import com.example.blog.common.pagenation.BlogSortBy;
import com.example.blog.domain.blog.dto.BlogWithStat;
import com.example.blog.domain.blog.entity.Blog;
import com.example.blog.domain.blog.entity.BlogVisibility;
import com.example.blog.domain.blog_stat.entity.BlogStat;
import com.example.blog.domain.member.entity.Member;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class PaginationUtilTest {

  @Test
  @DisplayName("인코딩과 디코딩을 할수 있다")
  void 인코딩과_디코딩을_할_수있다(){
    // given

    UUID id = UUID.randomUUID();
    long randomNumber = 123;
    String cursor = PaginationUtil.encode(randomNumber, id);

    //when
    Decoded decoded = PaginationUtil.decode(cursor);

    // then
    assertThat(decoded.id()).isEqualTo(id);
    assertThat(decoded.sortValue()).isEqualTo(randomNumber);
  }

  @Test
  @DisplayName("빈 리스트일 경우 PageInfo는 nextCursor=null, hasNext=false")
  void 빈_리스트도_파싱할_수_있다(){
    // given
    List<BlogWithStat> list = new ArrayList<>();
    PageInfo info = PaginationUtil.createPageForBlog(list, 10, BlogSortBy.VIEWS);

    // then
    assertThat(info.hasNext()).isFalse();
    assertThat(info.nextCursor()).isNull();
    assertThat(info.count()).isZero();
  }

  @Test
  @DisplayName("limit+1 개 데이터가 있을 때 hasNext=true")
  void createPageInfo_withHasNext() {
    List<BlogWithStat> list = new ArrayList<>();
    list.add(makeBlogWithStat(5L));
    list.add(makeBlogWithStat(10L));

    PageInfo pageInfo = PaginationUtil.createPageForBlog(list, 1, BlogSortBy.VIEWS);

    assertThat(pageInfo.hasNext()).isTrue();
    assertThat(pageInfo.count()).isEqualTo(1);
    assertThat(pageInfo.nextCursor()).contains(":");
  }

  @Test
  @DisplayName("마지막 엔티티의 sortValue와 id로 nextCursor가 생성된다")
  void createPageInfo_withLastEntity() {
    BlogWithStat b1 = makeBlogWithStat(50L);
    BlogWithStat b2 = makeBlogWithStat(100L);

    List<BlogWithStat> list = new ArrayList<>();
    list.add(b1);
    list.add(b2);

    PageInfo pageInfo = PaginationUtil.createPageForBlog(list, 2, BlogSortBy.VIEWS);

    assertThat(pageInfo.hasNext()).isFalse();
    assertThat(pageInfo.nextCursor()).isEqualTo(b2.stat().getViewCount() + ":" + b2.blog().getId());
  }
  private BlogWithStat makeBlogWithStat(long views) {
    UUID id = UUID.randomUUID();

    Member m = TestEntityFactory.createMember();
    Blog blog = new Blog(m, "title","description", BlogVisibility.PUBLIC, "slug", new HashSet<>());
    ReflectionTestUtils.setField(blog, "id", id);

    BlogStat stat = new BlogStat(
        id, blog, 0L, views, 0L, null, null
    );

    return new BlogWithStat(blog, stat);
  }

}
