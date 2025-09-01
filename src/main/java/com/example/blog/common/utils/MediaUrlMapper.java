package com.example.blog.common.utils;

import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MediaUrlMapper {

  @Value("${app.aws.s3.bucket-url}")
  private String base;

  @Named("toPublicUrl")
  public String toPublicUrl(String key) {
    if (key == null || key.isBlank()) return null;
    return base + key;
  }
}
