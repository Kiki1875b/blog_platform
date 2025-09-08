package com.example.blog.domain.member.repository;

import com.example.blog.domain.member.entity.Member;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class MemberRepositoryAdapter implements MemberRepositoryPort{

  private final MemberRepository memberRepository;
  @Override
  public Optional<Member> findByEmail(String email) {
    return memberRepository.findByEmail(email);
  }

  @Override
  public Optional<Member> findById(UUID uuid) {
    return memberRepository.findById(uuid);
  }

  @Override
  public Member save(Member member) {
    return memberRepository.save(member);
  }
}
