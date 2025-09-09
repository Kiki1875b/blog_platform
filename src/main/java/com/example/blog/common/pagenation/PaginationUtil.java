package com.example.blog.common.pagenation;

import com.example.blog.domain.base.BaseEntity;
import java.util.List;
import java.util.UUID;

public class PaginationUtil {
  private PaginationUtil() {}
  public static <T extends BaseEntity> PageInfo createPageInfo(List<T> entities, long limit) {
    boolean hasNext = hasNext(entities.size(), (int) limit);

    if (hasNext) {
      entities.remove(entities.size() - 1);
    }

    UUID lastId = entities.isEmpty() ? null : entities.get(entities.size() - 1).getId();

    return new PageInfo(
        entities.size(),
        limit,
        0, // TODO: totalCount 필요하다면 별도 조회
        lastId,
        hasNext
    );
  }

  private static boolean hasNext(int size, int limit) {
    return limit < size;
  }
}
