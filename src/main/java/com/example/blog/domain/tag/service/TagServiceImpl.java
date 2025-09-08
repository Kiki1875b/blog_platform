package com.example.blog.domain.tag.service;

import com.example.blog.domain.tag.entity.Tag;
import com.example.blog.domain.tag.repository.TagRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService{

  private final TagRepository tagRepository;
  @Override
  @Transactional
  public List<Tag> getOrCreateTags(List<String> tags) {
    Set<Tag> existingTags = tagRepository.findAllByNameIn(tags);
    Set<String> existingNames = existingTags.stream().map(Tag::getName).collect(Collectors.toSet());

    List<Tag> newTags = tags.stream().filter(name -> !existingNames.contains(name)).map(Tag::new).toList();;

    List<Tag> savedNewTags = tagRepository.saveAll(newTags); // TODO: batch insert?
    return Stream.concat(existingTags.stream(), savedNewTags.stream()).toList();
  }
}
