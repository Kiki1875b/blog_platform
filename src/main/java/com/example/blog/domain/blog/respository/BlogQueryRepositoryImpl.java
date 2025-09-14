package com.example.blog.domain.blog.respository;


import com.example.blog.common.pagenation.Direction;
import com.example.blog.common.pagenation.PaginationUtil;
import com.example.blog.domain.blog.dto.BlogPaginationRequest;
import com.example.blog.domain.blog.dto.BlogWithStat;
import com.example.blog.domain.blog.entity.QBlog;
import com.example.blog.domain.blog_stat.entity.QBlogStat;
import com.example.blog.domain.blog_tag.entity.QBlogTag;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BlogQueryRepositoryImpl implements BlogQueryRepository{
  private final JPAQueryFactory queryFactory;

  /**
   * 특정 Member 의 블로그 목록을 커서 기반 페이징 조회한다
   */
  public List<BlogWithStat> findByMemberIdAndQuery(UUID memberId, BlogPaginationRequest request){
    QBlog blog = QBlog.blog;
    QBlogStat blogStat = QBlogStat.blogStat;
    QBlogTag blogTag = QBlogTag.blogTag;

    BooleanBuilder where = new BooleanBuilder();

    where.and(blog.member.id.eq(memberId));

    // === 정렬 기준 필드 선택 ===
    var sortField = switch (request.sortBy()) {
      case VIEWS -> blogStat.viewCount;
      case POSTS -> blogStat.postCount;
      case FOLLOWERS -> blogStat.followerCount;
    };

    PaginationUtil.Decoded decoded = PaginationUtil.decode(request.cursor());

    if (decoded != null) {
      long lastValue = decoded.sortValue();
      UUID lastId = decoded.id();

      BooleanBuilder cursorCondition = new BooleanBuilder();
      if(request.direction() == Direction.DESC){
        cursorCondition.or(sortField.lt(lastValue));
        cursorCondition.or(sortField.eq(lastValue).and(blog.id.lt(lastId)));
      } else {
        cursorCondition.or(sortField.gt(lastValue));
        cursorCondition.or(sortField.eq(lastValue).and(blog.id.gt(lastId)));
      }
      where.and(cursorCondition);
    }

    OrderSpecifier<?> primary = (request.direction() == Direction.ASC) ? sortField.asc() : sortField.desc();
    OrderSpecifier<?> secondary = (request.direction() == Direction.ASC) ? blog.id.asc() : blog.id.desc();

    return queryFactory
        .select(Projections.constructor(BlogWithStat.class, blog, blogStat))
        .from(blog)
        .join(blogStat).on(blogStat.blog.eq(blog)).fetchJoin()
        .where(where)
        .orderBy(primary, secondary)
        .limit(request.limit() + 1)
        .fetch();

  }
}
