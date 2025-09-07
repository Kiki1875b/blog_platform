package com.example.blog.domain.tag.repository;

import com.example.blog.domain.tag.entity.Tag;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, UUID> {

  Set<Tag> findAllByNameIn(List<String> names);
}
