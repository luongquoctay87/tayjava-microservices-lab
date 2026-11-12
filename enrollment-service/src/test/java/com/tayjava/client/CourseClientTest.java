package com.tayjava.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.tayjava.common.enums.ErrorCode;
import com.tayjava.common.exception.ErrorException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class CourseClientTest {

  @Test
  void restClientReadsPublishedFromEnvelopeData() {
    RestClient.Builder builder = RestClient.builder();
    MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
    server
        .expect(requestTo("/internal/courses/1"))
        .andRespond(
            withSuccess(
                """
                {"success":true,"code":"SUCCESS","data":{"id":1,"slug":"java-core","published":true}}
                """,
                MediaType.APPLICATION_JSON));

    new RestCourseClient(builder.build()).requirePublished(1L);
    server.verify();
  }

  @Test
  void restClientMissingCourseIsNotEnrollable() {
    RestClient.Builder builder = RestClient.builder();
    MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
    server.expect(requestTo("/internal/courses/9")).andRespond(withResourceNotFound());

    ErrorException error =
        assertThrows(
            ErrorException.class, () -> new RestCourseClient(builder.build()).requirePublished(9L));
    assertEquals(ErrorCode.COURSE_NOT_ENROLLABLE, error.getErrorCode());
  }

  @Test
  void restClientUnpublishedCourseIsNotEnrollable() {
    RestClient.Builder builder = RestClient.builder();
    MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
    server
        .expect(requestTo("/internal/courses/3"))
        .andRespond(
            withSuccess(
                """
                {"success":true,"data":{"id":3,"published":false}}
                """,
                MediaType.APPLICATION_JSON));

    ErrorException error =
        assertThrows(
            ErrorException.class, () -> new RestCourseClient(builder.build()).requirePublished(3L));
    assertEquals(ErrorCode.COURSE_NOT_ENROLLABLE, error.getErrorCode());
  }

  @Test
  void restClientMapsUnavailableStatus() {
    RestClient.Builder builder = RestClient.builder();
    MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
    server
        .expect(requestTo("/internal/courses/1"))
        .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

    ErrorException error =
        assertThrows(
            ErrorException.class, () -> new RestCourseClient(builder.build()).requirePublished(1L));
    assertEquals(ErrorCode.COURSE_UNAVAILABLE, error.getErrorCode());
    server.verify();
  }
}
