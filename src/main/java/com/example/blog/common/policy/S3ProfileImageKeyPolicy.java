package com.example.blog.common.policy;

import com.example.blog.common.exception.ErrorCode;
import com.example.blog.common.exception.MemberException;
import com.example.blog.common.policy.interf.ProfileImageKeyPolicy;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class S3ProfileImageKeyPolicy implements ProfileImageKeyPolicy {

  @Value("${cloud.aws.expected-prefix}")
  private String expectedPrefix;
  @Override
  public void validateOwnedKey(String key, UUID memberId) {
    if(key == null || key.isEmpty()) return;
    String expectedFullPrefix = expectedPrefix + memberId;
    if(!key.startsWith(expectedFullPrefix)) throw new MemberException(ErrorCode.INVALID_S3_PROFILE_KEY);
  }
}
