package com.example.blog.common.policy;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.blog.common.exception.ErrorCode;
import com.example.blog.common.exception.MemberException;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class S3ProfileImageKeyPolicyTest {

  S3ProfileImageKeyPolicy policy;
  String expectedPrefix = "https://test-url.com/";

  @BeforeEach
  void setUp(){
    policy = new S3ProfileImageKeyPolicy();
    ReflectionTestUtils.setField(policy, "expectedPrefix", expectedPrefix);
  }

  @Test
  @DisplayName("소유자의 url이 맞을경우 통과")
  void 소유자의_url이_맞을경우_통과(){
    UUID randomId = UUID.randomUUID();
    String key = expectedPrefix + randomId.toString();

    assertThatCode(() -> policy.validateOwnedKey(key, randomId))
        .doesNotThrowAnyException();
  }
  @Test
  @DisplayName("소유자의 url이 아닐 경우 예외 발생")
  void 소유자의_url이_아닐_경우_예외_발생() {
    UUID randomId = UUID.randomUUID();
    String anotherUser = UUID.randomUUID().toString();
    String key = expectedPrefix + anotherUser + "/profile.png";

    assertThatThrownBy(() -> policy.validateOwnedKey(key, randomId))
        .isInstanceOf(MemberException.class);
  }

  @Test
  @DisplayName("key가 null 또는 빈 문자열일 경우 통과")
  void key가_null_또는_빈문자열일_경우_통과() {
    UUID randomId = UUID.randomUUID();

    assertThatCode(() -> policy.validateOwnedKey(null, randomId))
        .doesNotThrowAnyException();

    assertThatCode(() -> policy.validateOwnedKey("", randomId))
        .doesNotThrowAnyException();
  }
}
