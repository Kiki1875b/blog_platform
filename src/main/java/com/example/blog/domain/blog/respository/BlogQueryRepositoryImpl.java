package com.example.blog.domain.blog.respository;


import com.example.blog.common.pagenation.Direction;
import com.example.blog.domain.blog.dto.BlogPaginationRequest;
import com.example.blog.domain.blog.entity.Blog;
import com.example.blog.domain.blog.entity.QBlog;
import com.example.blog.domain.blog_stat.entity.QBlogStat;
import com.example.blog.domain.blog_tag.entity.QBlogTag;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BlogQueryRepositoryImpl implements BlogQueryRepository{
  private final JPAQueryFactory queryFactory;

  public List<Blog> findByMemberIdAndQuery(UUID memberId, BlogPaginationRequest request){
    QBlog blog = QBlog.blog;
    QBlogStat blogStat = QBlogStat.blogStat;
    QBlogTag blogTag = QBlogTag.blogTag;

    BooleanBuilder builder = new BooleanBuilder();
    builder.and(blog.member.id.eq(memberId));

    OrderSpecifier<?> orderSpecifier = switch (request.sortBy()) {
      case VIEWS -> request.direction() == Direction.ASC ?
          blogStat.viewCount.asc() : blogStat.viewCount.desc();
      case POSTS -> request.direction() == Direction.ASC ?
          blogStat.postCount.asc() : blogStat.postCount.desc();
      case FOLLOWERS -> request.direction() == Direction.ASC ?
          blogStat.followerCount.asc() : blogStat.followerCount.desc();
    };

    return queryFactory
        .selectFrom(blog)
        .join(blogStat).on(blog.id.eq(blogStat.blog.id)).fetchJoin()
        .join(blogTag).on(blog.id.eq(blogTag.blog.id)).fetchJoin()
        .where(builder)
        .orderBy(orderSpecifier)
        .limit(request.limit() + 1) // for HasNext
        .fetch();
  }
}
