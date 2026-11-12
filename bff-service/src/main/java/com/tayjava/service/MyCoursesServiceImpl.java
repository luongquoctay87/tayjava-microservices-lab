package com.tayjava.service;

import com.tayjava.cache.CourseCache;
import com.tayjava.cache.CourseView;
import com.tayjava.client.EnrollmentApiClient;
import com.tayjava.client.OrderApiClient;
import com.tayjava.common.dto.bff.MyCourseResp;
import com.tayjava.common.exception.ErrorException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "MyCoursesService")
public class MyCoursesServiceImpl implements MyCoursesService {

  private final EnrollmentApiClient enrollmentApiClient;
  private final CourseCache courseCache;
  private final OrderApiClient orderApiClient;

  @Override
  public List<MyCourseResp> listMyCourses(String studentUsername) {
    List<EnrollmentApiClient.EnrollmentItem> enrollments =
        enrollmentApiClient.listByStudent(studentUsername);
    List<MyCourseResp> result = new ArrayList<>(enrollments.size() + 4);
    for (EnrollmentApiClient.EnrollmentItem enrollment : enrollments) {
      CourseView course = courseCache.getCourse(enrollment.getCourseId());
      result.add(
          MyCourseResp.builder()
              .enrollmentId(enrollment.getId())
              .courseId(course.id())
              .slug(course.slug())
              .title(course.title())
              .instructorUsername(course.instructorUsername())
              .status(enrollment.getStatus())
              .build());
    }

    for (OrderApiClient.OrderItem pending : orderApiClient.listFulfillingByStudent(studentUsername)) {
      result.add(pendingCheckoutRow(pending));
    }

    log.info("Composed {} My Courses rows for student={}", result.size(), studentUsername);
    return result;
  }

  private MyCourseResp pendingCheckoutRow(OrderApiClient.OrderItem pending) {
    String title = "Opening your course…";
    String slug = null;
    String instructor = null;
    try {
      CourseView course = courseCache.getCourse(pending.getCourseId());
      title = course.title();
      slug = course.slug();
      instructor = course.instructorUsername();
    } catch (ErrorException ignored) {
      // Course lag or outage must not invent an enrollment; keep the FULFILLING banner.
    }
    return MyCourseResp.builder()
        .orderId(pending.getId())
        .courseId(pending.getCourseId())
        .slug(slug)
        .title(title)
        .instructorUsername(instructor)
        .message("Order #" + pending.getId() + " is being processed")
        .build();
  }
}
