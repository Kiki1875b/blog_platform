package com.example.blog.domain.member.repository;

import com.example.blog.domain.member.entity.Member;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, UUID> {
  Optional<Member> findByEmail(String email);
}
