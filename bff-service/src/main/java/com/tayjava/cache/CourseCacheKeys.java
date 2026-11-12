package com.tayjava.cache;

/** Shared Redis key layout for course metadata (BFF cache + course-service invalidation). */
public final class CourseCacheKeys {

  public static final String PREFIX = "bff:course:v1:";

  private CourseCacheKeys() {}

  public static String byId(Long id) {
    return PREFIX + id;
  }
}
