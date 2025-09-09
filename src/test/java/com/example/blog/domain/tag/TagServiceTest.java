package com.example.blog.domain.tag;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.example.blog.domain.tag.entity.Tag;
import com.example.blog.domain.tag.repository.TagRepository;
import com.example.blog.domain.tag.service.TagServiceImpl;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TagServiceTest {
  @Mock
  TagRepository tagRepository;

  @InjectMocks
  TagServiceImpl tagService;
  @Captor
  ArgumentCaptor<List<Tag>> tagListCaptor;

  @BeforeEach
  void setUp(){

  }


  @Test
  @DisplayName("테그를 불러오거나 생성할 수 있다")
  void 테그를_불러오거나_생성할_수_있다(){
    // given
    List<String> req = List.of("tag1", "tag2");
    given(tagRepository.findAllByNameIn(req)).willReturn(Set.of(new Tag("tag1")));
    given(tagRepository.saveAll(tagListCaptor.capture()))
        .willAnswer(invocation -> invocation.getArgument(0));

    //when
    List<Tag> result = tagService.getOrCreateTags(req);

    //then
    List<Tag> captured = tagListCaptor.getValue();

    assertThat(captured).hasSize(1);
    assertThat(captured.get(0).getName()).isEqualTo("tag2");

    // 최종 결과에는 기존 tag1과 새로 저장된 tag2가 모두 포함되어야 함
    assertThat(result.stream().map(Tag::getName).collect(Collectors.toSet()))
        .containsExactlyInAnyOrder("tag1", "tag2");

    // saveAll 호출 검증
    verify(tagRepository).saveAll(captured);

  }

}
