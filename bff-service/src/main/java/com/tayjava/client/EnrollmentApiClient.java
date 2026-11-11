package com.tayjava.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tayjava.common.ApiVersions;
import com.tayjava.common.enums.EEnrollmentStatus;
import com.tayjava.common.enums.ErrorCode;
import com.tayjava.common.exception.ErrorException;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class EnrollmentApiClient {

  private final RestClient enrollmentRestClient;

  public EnrollmentApiClient(@Qualifier("enrollmentRestClient") RestClient enrollmentRestClient) {
    this.enrollmentRestClient = enrollmentRestClient;
  }

  public List<EnrollmentItem> listByStudent(String studentUsername) {
    try {
      EnrollmentEnvelope body =
          enrollmentRestClient
              .get()
              .uri(
                  uriBuilder ->
                      uriBuilder
                          .path(ApiVersions.PATH_V1 + "/enrollments")
                          .queryParam("student", studentUsername)
                          .build())
              .header(ApiVersions.HEADER, ApiVersions.V1)
              .retrieve()
              .body(EnrollmentEnvelope.class);
      if (body == null || body.getData() == null) {
        throw new ErrorException(ErrorCode.BAD_GATEWAY, "Empty enrollment response");
      }
      return body.getData();
    } catch (ErrorException ex) {
      throw ex;
    } catch (RestClientException ex) {
      throw new ErrorException(ErrorCode.BAD_GATEWAY, "Enrollment service unavailable");
    }
  }

  @Getter
  @Setter
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class EnrollmentEnvelope {
    private List<EnrollmentItem> data;
  }

  @Getter
  @Setter
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class EnrollmentItem {
    private Long id;
    private String studentUsername;
    private Long courseId;
    private EEnrollmentStatus status;
  }
}
