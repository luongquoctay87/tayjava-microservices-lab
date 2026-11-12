package com.tayjava.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tayjava.client.CourseApiClient;
import com.tayjava.common.enums.ErrorCode;
import com.tayjava.common.exception.ErrorException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class CourseCacheTest {

  @Mock private StringRedisTemplate redis;
  @Mock private ValueOperations<String, String> values;
  @Mock private CourseApiClient courseApiClient;

  private CourseCache courseCache;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    when(redis.opsForValue()).thenReturn(values);
    courseCache = new CourseCache(redis, objectMapper, courseApiClient);
  }

  @Test
  void cacheHitSkipsCourseClient() {
    CourseView view = new CourseView(1L, "java-core", "Java Core", "bao.teacher", true);
    when(values.get("bff:course:v1:1")).thenReturn(objectMapper.writeValueAsString(view));

    CourseView result = courseCache.getCourse(1L);

    assertThat(result.title()).isEqualTo("Java Core");
    verify(courseApiClient, never()).getById(any());
  }

  @Test
  void cacheMissLoadsAndStores() {
    when(values.get("bff:course:v1:2")).thenReturn(null);
    CourseApiClient.CourseItem item = new CourseApiClient.CourseItem();
    item.setId(2L);
    item.setSlug("spring-boot");
    item.setTitle("Spring Boot");
    item.setInstructorUsername("bao.teacher");
    item.setPublished(true);
    when(courseApiClient.getById(2L)).thenReturn(item);

    CourseView result = courseCache.getCourse(2L);

    assertThat(result.slug()).isEqualTo("spring-boot");
    ArgumentCaptor<String> json = ArgumentCaptor.forClass(String.class);
    verify(values).set(eq("bff:course:v1:2"), json.capture(), any(Duration.class));
    assertThat(json.getValue()).contains("spring-boot");
  }

  @Test
  void redisReadFailureFallsThroughToCourse() {
    when(values.get(anyString())).thenThrow(new QueryTimeoutException("redis down"));
    CourseApiClient.CourseItem item = new CourseApiClient.CourseItem();
    item.setId(3L);
    item.setSlug("redis-fallback");
    item.setTitle("Fallback");
    item.setInstructorUsername("bao.teacher");
    item.setPublished(true);
    when(courseApiClient.getById(3L)).thenReturn(item);

    assertThat(courseCache.getCourse(3L).title()).isEqualTo("Fallback");
  }

  @Test
  void courseFailurePropagates() {
    when(values.get(anyString())).thenReturn(null);
    when(courseApiClient.getById(9L))
        .thenThrow(new ErrorException(ErrorCode.COURSE_UNAVAILABLE, "down"));

    assertThatThrownBy(() -> courseCache.getCourse(9L))
        .isInstanceOf(ErrorException.class)
        .extracting(ex -> ((ErrorException) ex).getErrorCode())
        .isEqualTo(ErrorCode.COURSE_UNAVAILABLE);
  }

  @Test
  void singleFlightSharesOneUpstreamCall() throws Exception {
    when(values.get(anyString())).thenReturn(null);
    AtomicInteger calls = new AtomicInteger();
    when(courseApiClient.getById(5L))
        .thenAnswer(
            inv -> {
              calls.incrementAndGet();
              Thread.sleep(50);
              CourseApiClient.CourseItem item = new CourseApiClient.CourseItem();
              item.setId(5L);
              item.setSlug("shared");
              item.setTitle("Shared");
              item.setInstructorUsername("bao.teacher");
              item.setPublished(true);
              return item;
            });

    Thread t1 = new Thread(() -> courseCache.getCourse(5L));
    Thread t2 = new Thread(() -> courseCache.getCourse(5L));
    t1.start();
    t2.start();
    t1.join();
    t2.join();

    assertThat(calls.get()).isEqualTo(1);
  }
}
