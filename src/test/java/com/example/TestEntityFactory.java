package com.example;

import com.example.blog.common.enumerated.MemberStatus;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.entity.MemberRole;
import java.util.UUID;
import org.springframework.test.util.ReflectionTestUtils;

public class TestEntityFactory {

  public static Member createMember(){
    Member ret = new Member("test", "pwd", "nick", null, null, MemberStatus.ACTIVE, null, "name", MemberRole.USER);
    ReflectionTestUtils.setField(ret, "id", UUID.randomUUID());
    return ret;
  }

}
