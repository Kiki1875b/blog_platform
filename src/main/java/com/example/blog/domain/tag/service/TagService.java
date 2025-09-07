package com.example.blog.domain.tag.service;

import com.example.blog.domain.tag.entity.Tag;
import java.util.List;

public interface TagService {
  List<Tag> createTags(List<String> tags);
}
