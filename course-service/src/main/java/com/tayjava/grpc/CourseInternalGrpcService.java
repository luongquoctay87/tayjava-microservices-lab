package com.tayjava.grpc;

import com.tayjava.common.grpc.course.CourseInternalGrpc;
import com.tayjava.common.grpc.course.GetCourseRequest;
import com.tayjava.common.grpc.course.GetCourseResponse;
import com.tayjava.entity.Course;
import com.tayjava.repository.CourseRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class CourseInternalGrpcService extends CourseInternalGrpc.CourseInternalImplBase {

  private final CourseRepository courseRepository;

  @Override
  public void getCourse(GetCourseRequest request, StreamObserver<GetCourseResponse> responseObserver) {
    GetCourseResponse.Builder builder = GetCourseResponse.newBuilder();
    courseRepository
        .findById(request.getCourseId())
        .ifPresentOrElse(
            course -> responseObserver.onNext(toResponse(course)),
            () -> responseObserver.onNext(builder.setFound(false).build()));
    responseObserver.onCompleted();
  }

  private static GetCourseResponse toResponse(Course course) {
    return GetCourseResponse.newBuilder()
        .setFound(true)
        .setId(course.getId())
        .setSlug(course.getSlug() == null ? "" : course.getSlug())
        .setTitle(course.getTitle() == null ? "" : course.getTitle())
        .setPublished(course.isPublished())
        .build();
  }
}
