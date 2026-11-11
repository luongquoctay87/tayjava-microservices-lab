package com.tayjava.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tayjava.common.ApiVersions;
import com.tayjava.common.enums.ErrorCode;
import com.tayjava.common.exception.ErrorException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class CourseApiClient {

  private final RestClient courseRestClient;

  public CourseApiClient(@Qualifier("courseRestClient") RestClient courseRestClient) {
    this.courseRestClient = courseRestClient;
  }

  public CourseItem getById(Long courseId) {
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
      throw new ErrorException(ErrorCode.COURSE_NOT_FOUND);
    } catch (ErrorException ex) {
      throw ex;
    } catch (RestClientException ex) {
      throw new ErrorException(ErrorCode.COURSE_UNAVAILABLE, "Course service unavailable");
    }
  }

  @Getter
  @Setter
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class CourseEnvelope {
    private CourseItem data;
  }

  @Getter
  @Setter
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class CourseItem {
    private Long id;
    private String slug;
    private String title;
    private String instructorUsername;
    private Boolean published;
  }
}
