package com.tayjava.client;

import com.tayjava.common.ApiVersions;
import com.tayjava.common.enums.ErrorCode;
import com.tayjava.common.exception.ErrorException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Course transport backed by {@link WebClient}. Enrollment remains servlet-based, so the reactive
 * chain is blocked at the boundary. Retry / CB / bulkhead live in {@link ResilientCourseClient}.
 */
@Component
@ConditionalOnProperty(prefix = "tayjava.clients", name = "course-transport", havingValue = "web")
public class WebCourseClient implements CourseTransport {

  private final WebClient courseWebClient;

  public WebCourseClient(WebClient courseWebClient) {
    this.courseWebClient = courseWebClient;
  }

  @Override
  public void requirePublished(Long courseId) {
    CoursePayload course = fetchOnce(courseId);
    if (course.getPublished() == null || !course.getPublished()) {
      throw new ErrorException(ErrorCode.COURSE_NOT_ENROLLABLE, "Course is not published");
    }
  }

  private CoursePayload fetchOnce(Long courseId) {
    try {
      CourseEnvelope body =
          courseWebClient
              .get()
              .uri("/internal/courses/{id}", courseId)
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
                          new ErrorException(
                              ErrorCode.COURSE_UNAVAILABLE, "Course service unavailable")))
              .bodyToMono(CourseEnvelope.class)
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
}
