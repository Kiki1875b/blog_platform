package com.example.blog.common.pagenation;

import java.util.List;

public record PaginatedResponse<T>(
    List<T> items,
    PageInfo pageInfo
) {

}
