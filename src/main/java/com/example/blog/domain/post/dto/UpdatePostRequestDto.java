package com.example.blog.domain.post.dto;

import com.example.blog.domain.post.entity.PostState;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePostRequestDto {
    private String title;
    private String content;
    private PostState status;
    private List<String> tags;
}
