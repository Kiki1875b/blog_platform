package com.example.blog.common.pagenation;

import com.example.blog.domain.blog.dto.BlogWithStat;
import java.util.List;
import java.util.UUID;

public class PaginationUtil {
  private PaginationUtil() {}

  public static PageInfo createPageForBlog(List<BlogWithStat> entities, long limit, SortBy sortBy) {
    boolean hasNext = hasNext(entities.size(), (int) limit);
    if (hasNext) {
      entities.remove(entities.size() - 1);
    }
    if (entities.isEmpty()) {
      return new PageInfo(0, limit, 0, null, false);
    }

    BlogWithStat last = entities.get(entities.size() - 1);
    UUID lastId = last.blog().getId();
    long sortValue = switch (sortBy) {
      case VIEWS -> last.stat().getViewCount();
      case POSTS -> last.stat().getPostCount();
      case FOLLOWERS -> last.stat().getFollowerCount();
    };

    String nextCursor = encode(sortValue, lastId);
    return new PageInfo(entities.size(), limit, 0, nextCursor, hasNext);
  }

  private static boolean hasNext(int size, int limit) {
    return limit < size;
  }

  public static String encode(long sortValue, UUID id) { // todo: Base64
    return sortValue + ":" + id;
  }

  public static Decoded decode(String cursor) {
    if (cursor == null || cursor.isBlank()) return null;
    String[] parts = cursor.split(":");
    if (parts.length != 2) throw new IllegalArgumentException("Invalid cursor format");
    long sortValue = Long.parseLong(parts[0]);
    UUID id = UUID.fromString(parts[1]);
    return new Decoded(sortValue, id);
  }

  public record Decoded(long sortValue, UUID id) {}
}
