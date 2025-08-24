package com.example.blog.auth.blacklist;

import java.time.Instant;

public interface Blacklist {

  void addToBlackList(String token, Instant expTime);
  boolean exists(String token);

}
