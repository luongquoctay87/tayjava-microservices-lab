package com.tayjava.client;

import com.tayjava.common.ApiVersions;
import com.tayjava.common.enums.ErrorCode;
import com.tayjava.common.exception.ErrorException;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

/**
 * Course client backed by {@link WebClient}. Enrollment remains a servlet app, so the reactive
 * chain is blocked at the boundary — useful for comparing APIs side-by-side with RestClient.
 */
@Component
@ConditionalOnProperty(prefix = "tayjava.clients", name = "course-http-client", havingValue = "web")
public class WebCourseClient implements CourseClient {

  private final WebClient courseWebClient;
  private final int maxAttempts;
  private final Duration backoff;

  public WebCourseClient(
      WebClient courseWebClient,
      @Value("${tayjava.clients.course-retry-max-attempts}") int maxAttempts,
      @Value("${tayjava.clients.course-retry-backoff}") Duration backoff) {
    this.courseWebClient = courseWebClient;
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
    try {
      CourseEnvelope body =
          courseWebClient
              .get()
              .uri(ApiVersions.PATH_V1 + "/courses/{id}", courseId)
              .header(ApiVersions.HEADER, ApiVersions.V1)
              .retrieve()
              .onStatus(
                  status -> status.value() == 404,
                  response ->
                      Mono.error(
                          new ErrorException(ErrorCode.COURSE_NOT_ENROLLABLE, "Course not found")))
              .onStatus(
                  HttpStatusCode::is5xxServerError,
                  response ->
                      Mono.error(
                          new WebClientResponseException(
                              response.statusCode().value(),
                              "Course upstream error",
                              null,
                              null,
                              null)))
              .bodyToMono(CourseEnvelope.class)
              .retryWhen(
                  Retry.backoff(Math.max(0, maxAttempts - 1), backoff)
                      .filter(this::isTransient)
                      .onRetryExhaustedThrow(
                          (spec, signal) ->
                              new ErrorException(
                                  ErrorCode.COURSE_UNAVAILABLE, "Course service unavailable")))
              .block();
      if (body == null || body.getData() == null) {
        throw new ErrorException(ErrorCode.BAD_GATEWAY, "Empty course response");
      }
      return body.getData();
    } catch (ErrorException ex) {
      throw ex;
    } catch (WebClientResponseException.NotFound ex) {
      throw new ErrorException(ErrorCode.COURSE_NOT_ENROLLABLE, "Course not found");
    } catch (WebClientResponseException | WebClientRequestException ex) {
      throw new ErrorException(ErrorCode.COURSE_UNAVAILABLE, "Course service unavailable");
    } catch (RuntimeException ex) {
      if (ex.getCause() instanceof ErrorException nested) {
        throw nested;
      }
      throw new ErrorException(ErrorCode.BAD_GATEWAY, "Unexpected response from course service");
    }
  }

  private boolean isTransient(Throwable ex) {
    if (ex instanceof WebClientResponseException response) {
      int status = response.getStatusCode().value();
      return status == 502 || status == 503 || status == 504;
    }
    return ex instanceof WebClientRequestException;
  }
}
