package com.example.blog.common.pagenation;

import java.util.UUID;

public record PageInfo(
    long count,
    long limit,
    long total,
    UUID nextCursor,
    boolean hasNext
) {

}
