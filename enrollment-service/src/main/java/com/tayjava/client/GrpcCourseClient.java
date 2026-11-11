package com.tayjava.client;

import com.tayjava.common.enums.ErrorCode;
import com.tayjava.common.exception.ErrorException;
import com.tayjava.common.grpc.course.CourseInternalGrpc;
import com.tayjava.common.grpc.course.GetCourseRequest;
import com.tayjava.common.grpc.course.GetCourseResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** Internal Course lookup over gRPC (public REST on Course stays untouched). */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
    prefix = "tayjava.clients",
    name = "course-transport",
    havingValue = "grpc",
    matchIfMissing = true)
public class GrpcCourseClient implements CourseTransport {

  private final CourseInternalGrpc.CourseInternalBlockingStub courseInternalStub;

  @Override
  public void requirePublished(Long courseId) {
    try {
      GetCourseResponse response =
          courseInternalStub.getCourse(GetCourseRequest.newBuilder().setCourseId(courseId).build());
      if (!response.getFound()) {
        throw new ErrorException(ErrorCode.COURSE_NOT_ENROLLABLE, "Course not found");
      }
      if (!response.getPublished()) {
        throw new ErrorException(ErrorCode.COURSE_NOT_ENROLLABLE, "Course is not published");
      }
    } catch (ErrorException ex) {
      throw ex;
    } catch (StatusRuntimeException ex) {
      if (ex.getStatus().getCode() == Status.Code.NOT_FOUND) {
        throw new ErrorException(ErrorCode.COURSE_NOT_ENROLLABLE, "Course not found");
      }
      throw new ErrorException(ErrorCode.COURSE_UNAVAILABLE, "Course service unavailable");
    }
  }
}
