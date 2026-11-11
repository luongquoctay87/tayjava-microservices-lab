package com.tayjava.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tayjava.common.enums.ErrorCode;
import com.tayjava.common.exception.ErrorException;
import com.tayjava.common.grpc.course.CourseInternalGrpc;
import com.tayjava.common.grpc.course.GetCourseRequest;
import com.tayjava.common.grpc.course.GetCourseResponse;
import org.junit.jupiter.api.Test;

class GrpcCourseClientTest {

  @Test
  void requirePublishedAcceptsPublishedCourse() {
    CourseInternalGrpc.CourseInternalBlockingStub stub =
        mock(CourseInternalGrpc.CourseInternalBlockingStub.class);
    when(stub.getCourse(any(GetCourseRequest.class)))
        .thenReturn(
            GetCourseResponse.newBuilder()
                .setFound(true)
                .setId(1)
                .setSlug("java-core")
                .setPublished(true)
                .build());

    new GrpcCourseClient(stub).requirePublished(1L);
  }

  @Test
  void requirePublishedRejectsMissingCourse() {
    CourseInternalGrpc.CourseInternalBlockingStub stub =
        mock(CourseInternalGrpc.CourseInternalBlockingStub.class);
    when(stub.getCourse(any(GetCourseRequest.class)))
        .thenReturn(GetCourseResponse.newBuilder().setFound(false).build());

    ErrorException error =
        assertThrows(ErrorException.class, () -> new GrpcCourseClient(stub).requirePublished(9L));
    assertEquals(ErrorCode.COURSE_NOT_ENROLLABLE, error.getErrorCode());
  }
}
