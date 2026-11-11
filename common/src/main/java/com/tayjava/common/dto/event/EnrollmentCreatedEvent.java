package com.tayjava.common.dto.event;

import java.time.Instant;

/**
 * Enrollment created event. schemaVersion 1 = original fields only; schemaVersion 2 adds optional
 * studentEmail. Consumers must tolerate missing optional fields (backward compatible).
 */
public record EnrollmentCreatedEvent(
    String eventId,
    Long enrollmentId,
    String studentUsername,
    Long courseId,
    String status,
    Instant occurredAt,
    Integer schemaVersion,
    String studentEmail) {

  public EnrollmentCreatedEvent {
    if (schemaVersion == null) {
      schemaVersion = 1;
    }
  }

  /** Convenience factory for current producers (v2). */
  public static EnrollmentCreatedEvent v2(
      String eventId,
      Long enrollmentId,
      String studentUsername,
      Long courseId,
      String status,
      Instant occurredAt,
      String studentEmail) {
    return new EnrollmentCreatedEvent(
        eventId, enrollmentId, studentUsername, courseId, status, occurredAt, 2, studentEmail);
  }
}
