package com.tayjava.service;

import com.tayjava.common.dto.training.enrollment.EnrollmentReq;
import com.tayjava.common.dto.training.enrollment.EnrollmentResp;
import java.util.List;

public interface EnrollmentService {

  EnrollmentResp enroll(EnrollmentReq request);

  List<EnrollmentResp> getByStudentUsername(String studentUsername);
}
