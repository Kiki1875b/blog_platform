package com.example.blog.common.policy.interf;

import java.util.UUID;

public interface ProfileImageKeyPolicy {
  void validateOwnedKey(String key, UUID memberId);
}
