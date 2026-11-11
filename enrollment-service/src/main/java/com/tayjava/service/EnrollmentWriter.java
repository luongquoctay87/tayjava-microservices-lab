package com.tayjava.service;

import com.tayjava.common.dto.training.enrollment.EnrollmentReq;
import com.tayjava.common.dto.training.enrollment.EnrollmentResp;
import com.tayjava.common.enums.EEnrollmentStatus;
import com.tayjava.common.enums.ErrorCode;
import com.tayjava.common.exception.ErrorException;
import com.tayjava.entity.Enrollment;
import com.tayjava.entity.IdempotencyRecord;
import com.tayjava.messaging.OutboxAppender;
import com.tayjava.repository.EnrollmentRepository;
import com.tayjava.repository.IdempotencyRecordRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "EnrollmentWriter")
public class EnrollmentWriter {

  private final EnrollmentRepository enrollmentRepository;
  private final IdempotencyRecordRepository idempotencyRecordRepository;
  private final OutboxAppender outboxAppender;

  @Transactional(readOnly = true)
  Optional<EnrollmentResp> findReplay(String scope, String key, String requestHash) {
    return idempotencyRecordRepository
        .findByScopeAndIdempotencyKey(scope, key)
        .map(
            record -> {
              if (!record.getRequestHash().equals(requestHash)) {
                throw new ErrorException(
                    ErrorCode.IDEMPOTENCY_KEY_CONFLICT,
                    "Idempotency-Key already used with a different enroll payload");
              }
              Enrollment enrollment =
                  enrollmentRepository
                      .findById(record.getEnrollmentId())
                      .orElseThrow(() -> new ErrorException(ErrorCode.DATA_NOT_FOUND));
              return toResp(enrollment);
            });
  }

  @Transactional
  public EnrollmentResp persistNew(
      EnrollmentReq request, String scope, String key, String requestHash) {
    Optional<EnrollmentResp> race = findReplay(scope, key, requestHash);
    if (race.isPresent()) {
      return race.get();
    }

    Enrollment created =
        enrollmentRepository.save(
            Enrollment.builder()
                .studentUsername(request.getStudentUsername())
                .courseId(request.getCourseId())
                .enrollStatus(EEnrollmentStatus.APPROVED)
                .build());
    idempotencyRecordRepository.save(
        IdempotencyRecord.builder()
            .scope(scope)
            .idempotencyKey(key)
            .requestHash(requestHash)
            .enrollmentId(created.getId())
            .build());
    EnrollmentResp resp = toResp(created);
    // Same DB transaction as enrollment — relay publishes to Kafka asynchronously.
    outboxAppender.appendEnrollmentCreated(resp);
    log.info(
        "Enrolled student={} courseId={} enrollmentId={} idempotencyKey={}",
        created.getStudentUsername(),
        created.getCourseId(),
        created.getId(),
        key);
    return resp;
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
