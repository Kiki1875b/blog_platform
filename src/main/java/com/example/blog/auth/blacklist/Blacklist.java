package com.example.blog.auth.blacklist;

public interface Blacklist {

  void addToBlackList(String token);
  boolean exists(String token);

}
