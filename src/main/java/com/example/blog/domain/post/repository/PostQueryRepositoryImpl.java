package com.example.blog.domain.post.repository;

import static com.querydsl.core.types.dsl.Expressions.numberTemplate;

import com.example.blog.common.pagenation.Direction;
import com.example.blog.common.pagenation.PaginationUtil;
import com.example.blog.domain.post.dto.PostPaginationRequest;
import com.example.blog.domain.post.dto.PostWithStat;
import com.example.blog.domain.post.entity.QPost;
import com.example.blog.domain.post_stat.entity.QPostStat;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostQueryRepositoryImpl implements PostQueryRepository {

  private final JPAQueryFactory queryFactory;
  @Override
  public List<PostWithStat> findByBlogIdAndQuery(UUID blogId, PostPaginationRequest request) {

    QPost post = QPost.post;
    QPostStat postStat = QPostStat.postStat;

    BooleanBuilder where = new BooleanBuilder();
    where.and(post.blog.id.eq(blogId));


    NumberTemplate<Long> createdAtEpoch = numberTemplate(Long.class,
        "extract(epoch from {0}) * 1000 AS bigint", post.createdAt);

    var sortField = switch (request.postSortBy()) {
      case VIEWS -> postStat.viewCount;
      case DATE -> createdAtEpoch;
      case LIKES -> postStat.likeCount;
    };

    PaginationUtil.Decoded decoded = PaginationUtil.decode(request.cursor());

    if(decoded != null) {
      long lastValue = decoded.sortValue();
      UUID lastId = decoded.id();

      BooleanBuilder cursorCondition = new BooleanBuilder();
      if(request.direction() == Direction.DESC){
        cursorCondition.or(sortField.lt(lastValue));
        cursorCondition.or(sortField.eq(lastValue).and(post.id.lt(lastId)));
      } else {
        cursorCondition.or(sortField.gt(lastValue));
        cursorCondition.or(sortField.eq(lastValue).and(post.id.gt(lastId)));
      }
      where.and(cursorCondition);
    }

    OrderSpecifier<?> primary = switch (request.postSortBy()) {
      case DATE -> (request.direction() == Direction.ASC) ? post.createdAt.asc() : post.createdAt.desc();
      case VIEWS -> (request.direction() == Direction.ASC) ? postStat.viewCount.asc() : postStat.viewCount.desc();
      case LIKES -> (request.direction() == Direction.ASC) ? postStat.likeCount.asc() : postStat.likeCount.desc();
    };
    OrderSpecifier<?> secondary =
        (request.direction() == Direction.ASC) ? post.id.asc() : post.id.desc();


    return queryFactory
        .select(Projections.constructor(PostWithStat.class, post, postStat))
        .from(post)
        .join(postStat).on(postStat.post.eq(post)).fetchJoin()
        .where(where)
        .orderBy(primary, secondary)
        .limit(request.limit() + 1)
        .fetch();
  }
}
