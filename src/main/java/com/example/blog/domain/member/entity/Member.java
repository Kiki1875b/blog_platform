package com.example.blog.domain.member.entity;

import com.example.blog.common.enumerated.MemberStatus;
import com.example.blog.common.enumerated.Provider;
import com.example.blog.domain.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class Member extends BaseEntity {

  @Column(nullable = false, unique = true)
  private String email;
  private String password;
  @Column(nullable = false, unique = true)
  private String nickname;
  @Enumerated(EnumType.STRING)
  private Provider provider;
  private String providerId;
  @Enumerated(EnumType.STRING)
  private MemberStatus status;
  @Column(nullable = true)
  private String profileUrl;
  @Column(nullable = false)
  private String name;

  public void updateStatus(MemberStatus status){
    this.status = status;
  }

  public void updatePassword(String password){
    this.password = password;
  }
}
