package com.tayjava.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Deletes the BFF course metadata key after a course write commits. Redis failure must not roll
 * back the domain write — TTL still bounds staleness.
 */
@Component
@RequiredArgsConstructor
@Slf4j(topic = "CourseCacheInvalidator")
public class CourseCacheInvalidator {

  static final String KEY_PREFIX = "bff:course:v1:";

  private final StringRedisTemplate redis;

  public void evict(Long courseId) {
    if (courseId == null) {
      return;
    }
    try {
      redis.delete(KEY_PREFIX + courseId);
      log.info("Evicted course cache key for id={}", courseId);
    } catch (DataAccessException ex) {
      log.warn("Course cache eviction failed id={}", courseId, ex);
    }
  }
}
