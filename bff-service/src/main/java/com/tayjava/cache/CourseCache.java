package com.tayjava.cache;

import com.tayjava.client.CourseApiClient;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/**
 * Cache-aside for Course metadata. Redis failures are treated as misses; Course errors still
 * propagate. Enrollment write decisions must not read this cache.
 */
@Component
@Slf4j(topic = "CourseCache")
public class CourseCache {

  private static final Duration BASE_TTL = Duration.ofSeconds(120);
  private static final long MAX_JITTER_SECONDS = 30;

  private final StringRedisTemplate redis;
  private final ObjectMapper objectMapper;
  private final CourseApiClient courseApiClient;
  private final ConcurrentHashMap<Long, CompletableFuture<CourseView>> inFlight =
      new ConcurrentHashMap<>();

  public CourseCache(
      StringRedisTemplate redis, ObjectMapper objectMapper, CourseApiClient courseApiClient) {
    this.redis = redis;
    this.objectMapper = objectMapper;
    this.courseApiClient = courseApiClient;
  }

  public CourseView getCourse(Long id) {
    CourseView cached = readCache(id);
    if (cached != null) {
      return cached;
    }
    CompletableFuture<CourseView> mine = new CompletableFuture<>();
    CompletableFuture<CourseView> leader = inFlight.putIfAbsent(id, mine);
    if (leader != null) {
      return awaitLeader(leader);
    }
    try {
      CourseApiClient.CourseItem item = courseApiClient.getById(id);
      CourseView view =
          new CourseView(
              item.getId(),
              item.getSlug(),
              item.getTitle(),
              item.getInstructorUsername(),
              Boolean.TRUE.equals(item.getPublished()));
      writeCache(id, view);
      mine.complete(view);
      return view;
    } catch (RuntimeException ex) {
      mine.completeExceptionally(ex);
      throw ex;
    } finally {
      inFlight.remove(id, mine);
    }
  }

  public void evict(Long id) {
    try {
      redis.delete(CourseCacheKeys.byId(id));
    } catch (DataAccessException ex) {
      log.warn("Cannot evict course cache id={}", id, ex);
    }
  }

  private CourseView awaitLeader(CompletableFuture<CourseView> leader) {
    try {
      return leader.join();
    } catch (CompletionException ex) {
      if (ex.getCause() instanceof RuntimeException cause) {
        throw cause;
      }
      throw ex;
    }
  }

  private CourseView readCache(Long id) {
    try {
      String json = redis.opsForValue().get(CourseCacheKeys.byId(id));
      return json == null ? null : objectMapper.readValue(json, CourseView.class);
    } catch (RuntimeException ex) {
      log.warn("Course cache read failed id={}", id, ex);
      return null;
    }
  }

  private void writeCache(Long id, CourseView view) {
    Duration ttl = BASE_TTL.plusSeconds(ThreadLocalRandom.current().nextLong(MAX_JITTER_SECONDS + 1));
    try {
      redis.opsForValue().set(CourseCacheKeys.byId(id), objectMapper.writeValueAsString(view), ttl);
    } catch (RuntimeException ex) {
      log.warn("Course cache write failed id={}", id, ex);
    }
  }
}
