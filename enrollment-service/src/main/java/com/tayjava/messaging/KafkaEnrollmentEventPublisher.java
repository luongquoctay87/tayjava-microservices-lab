package com.tayjava.messaging;

import com.tayjava.common.dto.event.EnrollmentCreatedEvent;
import com.tayjava.common.dto.training.enrollment.EnrollmentResp;
import com.tayjava.common.messaging.KafkaTopics;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "tayjava.kafka.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j(topic = "KafkaEnrollmentEventPublisher")
public class KafkaEnrollmentEventPublisher implements EnrollmentEventPublisher {

  private final KafkaTemplate<String, EnrollmentCreatedEvent> kafkaTemplate;
  private final AtomicReference<EnrollmentCreatedEvent> lastPublished = new AtomicReference<>();

  @Override
  public void publishEnrollmentCreated(EnrollmentResp enrollment) {
    EnrollmentCreatedEvent event =
        new EnrollmentCreatedEvent(
            UUID.randomUUID().toString(),
            enrollment.getId(),
            enrollment.getStudentUsername(),
            enrollment.getCourseId(),
            enrollment.getStatus().name(),
            Instant.now());
    kafkaTemplate.send(KafkaTopics.ENROLLMENT_EVENTS, String.valueOf(enrollment.getId()), event);
    lastPublished.set(event);
    log.info(
        "Published EnrollmentCreatedEvent eventId={} enrollmentId={} courseId={}",
        event.eventId(),
        event.enrollmentId(),
        event.courseId());
  }

  @Override
  public EnrollmentCreatedEvent lastPublished() {
    return lastPublished.get();
  }
}
