package com.tayjava.client;

import com.tayjava.common.ApiVersions;
import com.tayjava.common.enums.ErrorCode;
import com.tayjava.common.exception.ErrorException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/** Blocking Course transport backed by {@link RestClient}. Retry lives in Resilience4j. */
@Component
@ConditionalOnProperty(prefix = "tayjava.clients", name = "course-transport", havingValue = "rest")
public class RestCourseClient implements CourseTransport {

  private final RestClient courseRestClient;

  public RestCourseClient(RestClient courseRestClient) {
    this.courseRestClient = courseRestClient;
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
      throw new ErrorException(ErrorCode.COURSE_UNAVAILABLE, "Course service unavailable");
    } catch (ErrorException ex) {
      throw ex;
    } catch (RestClientException ex) {
      throw new ErrorException(ErrorCode.BAD_GATEWAY, "Unexpected response from course service");
    }
  }
}
