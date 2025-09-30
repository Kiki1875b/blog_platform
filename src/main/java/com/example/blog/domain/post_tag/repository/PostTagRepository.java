package com.example.blog.domain.post_tag.repository;

import com.example.blog.domain.post_tag.entity.PostTag;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTagRepository extends JpaRepository<PostTag, UUID> {

  List<PostTag> findAllByPost_IdIn(List<UUID> postIds);
}
