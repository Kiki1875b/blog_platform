package com.example.blog.domain.member.entity;

import com.example.blog.common.enumerated.MemberStatus;
import com.example.blog.common.enumerated.Provider;
import com.example.blog.domain.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseUpdatableEntity {

  @Column(nullable = false, unique = true)
  private String email;

  private String password;

  @Column(nullable = false)
  private String nickname;

  @Enumerated(EnumType.STRING)
  private Provider provider;

  private String providerId;

  @Enumerated(EnumType.STRING)
  private MemberStatus status = MemberStatus.ACTIVE;

  @Column(nullable = true)
  private String profileUrl;

  @Column(nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  private MemberRole role = MemberRole.USER;

  public void updateStatus(MemberStatus status){
    if(status == null) return;
    this.status = status;
  }

  public void updatePassword(String password){
    if(password == null || password.isEmpty()) return;
    this.password = password;
  }

  public void updateRole(MemberRole role){
    if(role == null) return;
    this.role = role;
  }

  public void updateNickname(String nickname){
    if(nickname == null || nickname.isEmpty()) return;
    this.nickname = nickname;
  }

  public void updateProfileUrl(String profileUrl){
    if(profileUrl == null || profileUrl.isEmpty()) return;
    this.profileUrl = profileUrl;
  }


}
