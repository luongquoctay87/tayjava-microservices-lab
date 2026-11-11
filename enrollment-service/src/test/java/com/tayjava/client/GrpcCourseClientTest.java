package com.tayjava.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.tayjava.common.enums.ErrorCode;
import com.tayjava.common.exception.ErrorException;
import com.tayjava.common.grpc.course.GetCourseResponse;
import org.junit.jupiter.api.Test;

class GrpcCourseClientTest {
  @Test
  void requirePublishedAcceptsPublishedCourse() {
    CourseGrpcResponses.ensurePublished(
        GetCourseResponse.newBuilder().setFound(true).setId(1).setSlug("java-core").setPublished(true).build());
  }
  @Test
  void requirePublishedRejectsMissingCourse() {
    ErrorException error = assertThrows(ErrorException.class,
        () -> CourseGrpcResponses.ensurePublished(GetCourseResponse.newBuilder().setFound(false).build()));
    assertEquals(ErrorCode.COURSE_NOT_ENROLLABLE, error.getErrorCode());
  }
}
