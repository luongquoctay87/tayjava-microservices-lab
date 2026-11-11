package com.tayjava.client;

import com.tayjava.common.ApiVersions;
import com.tayjava.common.enums.ErrorCode;
import com.tayjava.common.exception.ErrorException;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/** Blocking Course client backed by {@link RestClient}. */
@Component
@ConditionalOnProperty(prefix = "tayjava.clients", name = "course-transport", havingValue = "rest")
public class RestCourseClient implements CourseClient {

  private final RestClient courseRestClient;
  private final int maxAttempts;
  private final Duration backoff;

  public RestCourseClient(
      RestClient courseRestClient,
      @Value("${tayjava.clients.course-retry-max-attempts}") int maxAttempts,
      @Value("${tayjava.clients.course-retry-backoff}") Duration backoff) {
    this.courseRestClient = courseRestClient;
    this.maxAttempts = maxAttempts;
    this.backoff = backoff;
  }

  @Override
  public void requirePublished(Long courseId) {
    CoursePayload course = fetchWithRetry(courseId);
    if (course.getPublished() == null || !course.getPublished()) {
      throw new ErrorException(ErrorCode.COURSE_NOT_ENROLLABLE, "Course is not published");
    }
  }

  private CoursePayload fetchWithRetry(Long courseId) {
    for (int attempt = 1; ; attempt++) {
      try {
        return fetchOnce(courseId);
      } catch (HttpServerErrorException | ResourceAccessException ex) {
        if (!isTransient(ex) || attempt >= maxAttempts) {
          throw new ErrorException(ErrorCode.COURSE_UNAVAILABLE, "Course service unavailable");
        }
        sleepBeforeRetry(attempt);
      }
    }
  }

  private CoursePayload fetchOnce(Long courseId) {
    try {
      CourseEnvelope body =
          courseRestClient
              .get()
              .uri(ApiVersions.PATH_V1 + "/courses/{id}", courseId)
              .header(ApiVersions.HEADER, ApiVersions.V1)
              .retrieve()
              .body(CourseEnvelope.class);
      if (body == null || body.getData() == null) {
        throw new ErrorException(ErrorCode.BAD_GATEWAY, "Empty course response");
      }
      return body.getData();
    } catch (HttpClientErrorException.NotFound ex) {
      throw new ErrorException(ErrorCode.COURSE_NOT_ENROLLABLE, "Course not found");
    } catch (HttpServerErrorException | ResourceAccessException ex) {
      throw ex;
    } catch (ErrorException ex) {
      throw ex;
    } catch (RestClientException ex) {
      throw new ErrorException(ErrorCode.BAD_GATEWAY, "Unexpected response from course service");
    }
  }

  private static boolean isTransient(RestClientException ex) {
    if (ex instanceof HttpServerErrorException server) {
      int status = server.getStatusCode().value();
      return status == 502 || status == 503 || status == 504;
    }
    return true;
  }

  private void sleepBeforeRetry(int attempt) {
    long baseMillis = backoff.toMillis() * (1L << (attempt - 1));
    long lower = Math.max(1, baseMillis / 2);
    long upper = Math.max(lower + 1, baseMillis + 1);
    long withJitter = ThreadLocalRandom.current().nextLong(lower, upper);
    try {
      Thread.sleep(withJitter);
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      throw new ErrorException(
          ErrorCode.COURSE_UNAVAILABLE, "Interrupted while calling course service");
    }
  }
}
