package com.tayjava.service;

import com.tayjava.common.dto.training.enrollment.EnrollmentReq;
import com.tayjava.common.dto.training.enrollment.EnrollmentResp;
import com.tayjava.common.enums.EEnrollmentStatus;
import com.tayjava.entity.Enrollment;
import com.tayjava.repository.EnrollmentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "EnrollmentService")
public class EnrollmentServiceImpl implements EnrollmentService {

  private final EnrollmentRepository enrollmentRepository;

  @Override
  public EnrollmentResp enroll(EnrollmentReq request) {
    // Does not check that the course exists yet; lesson 8 calls course-service over HTTP.
    Enrollment created =
        enrollmentRepository.save(
            Enrollment.builder()
                .studentUsername(request.getStudentUsername())
                .courseId(request.getCourseId())
                .enrollStatus(EEnrollmentStatus.APPROVED)
                .build());
    log.info(
        "Enrolled student={} courseId={} enrollmentId={}",
        created.getStudentUsername(),
        created.getCourseId(),
        created.getId());
    return toResp(created);
  }

  @Override
  public List<EnrollmentResp> getByStudentUsername(String studentUsername) {
    return enrollmentRepository.findByStudentUsername(studentUsername).stream()
        .map(this::toResp)
        .toList();
  }

  private EnrollmentResp toResp(Enrollment enrollment) {
    return EnrollmentResp.builder()
        .id(enrollment.getId())
        .studentUsername(enrollment.getStudentUsername())
        .courseId(enrollment.getCourseId())
        .status(enrollment.getEnrollStatus())
        .build();
  }
}
