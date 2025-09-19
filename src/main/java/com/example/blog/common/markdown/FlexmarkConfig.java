package com.example.blog.common.markdown;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlexmarkConfig {

  @Bean
  public Parser parser(){
    return Parser.builder().build();
  }

  @Bean
  public HtmlRenderer renderer(){
    return HtmlRenderer.builder().escapeHtml(true).build();
  }
}
