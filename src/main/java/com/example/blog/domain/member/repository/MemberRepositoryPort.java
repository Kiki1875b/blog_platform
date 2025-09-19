package com.example.blog.domain.member.repository;

import com.example.blog.domain.member.entity.Member;
import java.util.Optional;
import java.util.UUID;

public interface MemberRepositoryPort {
  Optional<Member> findByEmail(String email);
  Optional<Member> findById(UUID uuid);
  Member save(Member member);
  Member findMemberProxy(UUID uuid);
}
