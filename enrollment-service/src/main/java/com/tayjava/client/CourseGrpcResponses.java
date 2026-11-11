package com.tayjava.client;

import com.tayjava.common.enums.ErrorCode;
import com.tayjava.common.exception.ErrorException;
import com.tayjava.common.grpc.course.GetCourseResponse;

/** Maps gRPC GetCourseResponse to enrollment rules (unit-testable without mocking final stubs). */
final class CourseGrpcResponses {
  private CourseGrpcResponses() {}
  static void ensurePublished(GetCourseResponse response) {
    if (!response.getFound()) {
      throw new ErrorException(ErrorCode.COURSE_NOT_ENROLLABLE, "Course not found");
    }
    if (!response.getPublished()) {
      throw new ErrorException(ErrorCode.COURSE_NOT_ENROLLABLE, "Course is not published");
    }
  }
}
