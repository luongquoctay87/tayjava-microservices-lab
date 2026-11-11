package com.tayjava.controller;

import com.tayjava.common.dto.ApiResp;
import com.tayjava.common.dto.Resp;
import com.tayjava.common.dto.training.enrollment.EnrollmentReq;
import com.tayjava.common.dto.training.enrollment.EnrollmentResp;
import com.tayjava.service.EnrollmentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/enrollments")
public class EnrollmentController {

  private final EnrollmentService enrollmentService;

  @GetMapping
  public ResponseEntity<Resp<List<EnrollmentResp>>> getByStudent(
      @RequestParam("student") String studentUsername) {
    // Stand-in for the current user. Lessons 28–29 take the username from the JWT, not the query.
    return ApiResp.ok(enrollmentService.getByStudentUsername(studentUsername));
  }

  @PostMapping
  public ResponseEntity<Resp<EnrollmentResp>> enroll(@Valid @RequestBody EnrollmentReq request) {
    return ApiResp.created(enrollmentService.enroll(request));
  }
}
