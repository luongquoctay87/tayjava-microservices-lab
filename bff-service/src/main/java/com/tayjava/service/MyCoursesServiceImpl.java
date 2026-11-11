package com.tayjava.service;

import com.tayjava.client.CourseApiClient;
import com.tayjava.client.EnrollmentApiClient;
import com.tayjava.common.dto.bff.MyCourseResp;
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
  private final CourseApiClient courseApiClient;

  @Override
  public List<MyCourseResp> listMyCourses(String studentUsername) {
    List<EnrollmentApiClient.EnrollmentItem> enrollments =
        enrollmentApiClient.listByStudent(studentUsername);
    List<MyCourseResp> result = new ArrayList<>(enrollments.size());
    for (EnrollmentApiClient.EnrollmentItem enrollment : enrollments) {
      CourseApiClient.CourseItem course = courseApiClient.getById(enrollment.getCourseId());
      result.add(
          MyCourseResp.builder()
              .enrollmentId(enrollment.getId())
              .courseId(course.getId())
              .slug(course.getSlug())
              .title(course.getTitle())
              .instructorUsername(course.getInstructorUsername())
              .status(enrollment.getStatus())
              .build());
    }
    log.info("Composed {} courses for student={}", result.size(), studentUsername);
    return result;
  }
}
