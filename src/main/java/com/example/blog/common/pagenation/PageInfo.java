package com.example.blog.common.pagenation;

public record PageInfo(
    long count,
    long limit,
    long total,
    String nextCursor,
    boolean hasNext
) {

}
