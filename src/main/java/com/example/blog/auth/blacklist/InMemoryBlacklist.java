package com.example.blog.auth.blacklist;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class InMemoryBlacklist implements Blacklist{

  private final Map<String, Instant> blacklist = new ConcurrentHashMap<>();
  @Override
  public void addToBlackList(String token, Instant expTime) {
    blacklist.put(token, expTime);
  }

  @Override
  public boolean exists(String token) {
    return blacklist.containsKey(token);
  }
}
