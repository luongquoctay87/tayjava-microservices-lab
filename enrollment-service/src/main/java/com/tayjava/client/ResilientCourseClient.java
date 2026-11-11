package com.tayjava.client;

import com.tayjava.common.enums.ErrorCode;
import com.tayjava.common.exception.ErrorException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Applies circuit breaker / retry / bulkhead around the selected {@link CourseTransport}
 * (grpc|rest|web).
 */
@Component
@RequiredArgsConstructor
public class ResilientCourseClient implements CourseClient {

  private final CourseTransport courseTransport;

  @Override
  @CircuitBreaker(name = "course", fallbackMethod = "unavailable")
  @Retry(name = "course")
  @Bulkhead(name = "course")
  public void requirePublished(Long courseId) {
    courseTransport.requirePublished(courseId);
  }

  @SuppressWarnings("unused")
  private void unavailable(Long courseId, CallNotPermittedException ex) {
    throw new ErrorException(ErrorCode.COURSE_UNAVAILABLE, "Course service unavailable");
  }

  @SuppressWarnings("unused")
  private void unavailable(Long courseId, Throwable ex) {
    if (ex instanceof ErrorException error) {
      throw error;
    }
    throw new ErrorException(ErrorCode.COURSE_UNAVAILABLE, "Course service unavailable");
  }
}
