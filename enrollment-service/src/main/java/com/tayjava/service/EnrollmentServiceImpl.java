package com.tayjava.service;

import com.tayjava.client.CourseClient;
import com.tayjava.common.dto.training.enrollment.EnrollmentReq;
import com.tayjava.common.dto.training.enrollment.EnrollmentResp;
import com.tayjava.common.enums.ErrorCode;
import com.tayjava.common.exception.ErrorException;
import com.tayjava.repository.EnrollmentRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "EnrollmentService")
public class EnrollmentServiceImpl implements EnrollmentService {

  static final String ENROLL_SCOPE = "ENROLL";

  private final EnrollmentRepository enrollmentRepository;
  private final EnrollmentWriter enrollmentWriter;
  private final CourseClient courseClient;
  private final PaymentStub paymentStub;

  @Override
  public EnrollmentResp enroll(EnrollmentReq request, String idempotencyKey) {
    if (!StringUtils.hasText(idempotencyKey)) {
      throw new ErrorException(ErrorCode.IDEMPOTENCY_KEY_REQUIRED, "Idempotency-Key is required");
    }
    String key = idempotencyKey.trim();
    String requestHash = hashRequest(request);

    Optional<EnrollmentResp> replay = enrollmentWriter.findReplay(ENROLL_SCOPE, key, requestHash);
    if (replay.isPresent()) {
      paymentStub.chargeOnce(key, request.getStudentUsername(), request.getCourseId());
      return replay.get();
    }

    // Network call outside a write transaction so a slow Course does not hold a DB connection.
    courseClient.requirePublished(request.getCourseId());
    paymentStub.chargeOnce(key, request.getStudentUsername(), request.getCourseId());
    return enrollmentWriter.persistNew(request, ENROLL_SCOPE, key, requestHash);
  }

  @Override
  @Transactional(readOnly = true)
  public List<EnrollmentResp> getByStudentUsername(String studentUsername) {
    return enrollmentRepository.findByStudentUsername(studentUsername).stream()
        .map(
            enrollment ->
                EnrollmentResp.builder()
                    .id(enrollment.getId())
                    .studentUsername(enrollment.getStudentUsername())
                    .courseId(enrollment.getCourseId())
                    .status(enrollment.getEnrollStatus())
                    .build())
        .toList();
  }

  private static String hashRequest(EnrollmentReq request) {
    String raw = request.getStudentUsername().trim() + "|" + request.getCourseId();
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      return HexFormat.of().formatHex(digest.digest(raw.getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException ex) {
      throw new IllegalStateException("SHA-256 unavailable", ex);
    }
  }
}
