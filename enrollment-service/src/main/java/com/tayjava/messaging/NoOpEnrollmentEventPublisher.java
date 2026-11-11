package com.tayjava.messaging;

import com.tayjava.common.dto.event.EnrollmentCreatedEvent;
import com.tayjava.common.dto.training.enrollment.EnrollmentResp;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Used when Kafka is disabled (unit tests). Records the last event for assertions without a
 * broker.
 */
@Component
@ConditionalOnProperty(name = "tayjava.kafka.enabled", havingValue = "false")
@Slf4j(topic = "NoOpEnrollmentEventPublisher")
public class NoOpEnrollmentEventPublisher implements EnrollmentEventPublisher {

  private final AtomicReference<EnrollmentCreatedEvent> lastPublished = new AtomicReference<>();

  @Override
  public void publishEnrollmentCreated(EnrollmentResp enrollment) {
    EnrollmentCreatedEvent event =
        new EnrollmentCreatedEvent(
            "noop-" + enrollment.getId(),
            enrollment.getId(),
            enrollment.getStudentUsername(),
            enrollment.getCourseId(),
            enrollment.getStatus().name(),
            Instant.now());
    lastPublished.set(event);
    log.debug("No-op publish enrollmentId={}", enrollment.getId());
  }

  @Override
  public EnrollmentCreatedEvent lastPublished() {
    return lastPublished.get();
  }
}
