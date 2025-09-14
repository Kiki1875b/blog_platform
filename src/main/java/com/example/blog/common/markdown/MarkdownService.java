package com.example.blog.common.markdown;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MarkdownService {

  private final Parser parser;
  private final HtmlRenderer renderer;

  public String toHtml(String markdown) {
    if (markdown == null || markdown.isBlank()) {
      return "";
    }

    return renderer.render(parser.parse(markdown));
  }
}
