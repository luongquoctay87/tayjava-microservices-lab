package com.tayjava.messaging;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import com.tayjava.common.dto.event.EnrollmentCreatedEvent;
import com.tayjava.common.dto.training.enrollment.EnrollmentResp;
import com.tayjava.entity.OutboxEvent;
import com.tayjava.repository.OutboxEventRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Writes an outbox row in the same transaction as the business write. The relay later publishes to
 * Kafka.
 */
@Component
@RequiredArgsConstructor
public class OutboxAppender {

  public static final String AGGREGATE_ENROLLMENT = "Enrollment";
  public static final String TYPE_ENROLLMENT_CREATED = "EnrollmentCreated";

  private final OutboxEventRepository outboxEventRepository;
  private final ObjectMapper objectMapper;

  public OutboxEvent appendEnrollmentCreated(EnrollmentResp enrollment) {
    EnrollmentCreatedEvent event =
        new EnrollmentCreatedEvent(
            UUID.randomUUID().toString(),
            enrollment.getId(),
            enrollment.getStudentUsername(),
            enrollment.getCourseId(),
            enrollment.getStatus().name(),
            Instant.now());
    try {
      return outboxEventRepository.save(
          OutboxEvent.builder()
              .eventId(event.eventId())
              .aggregateType(AGGREGATE_ENROLLMENT)
              .aggregateId(String.valueOf(enrollment.getId()))
              .eventType(TYPE_ENROLLMENT_CREATED)
              .payload(objectMapper.writeValueAsString(event))
              .status(OutboxEvent.Status.PENDING)
              .createdAt(Instant.now())
              .build());
    } catch (JacksonException ex) {
      throw new IllegalStateException("Failed to serialize enrollment outbox payload", ex);
    }
  }
}
