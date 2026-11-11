package com.tayjava.messaging;

import com.tayjava.common.dto.event.EnrollmentCreatedEvent;
import com.tayjava.common.messaging.KafkaTopics;
import com.tayjava.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "tayjava.kafka.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j(topic = "EnrollmentEventListener")
public class EnrollmentEventListener {

  private final OrderService orderService;

  @KafkaListener(topics = KafkaTopics.ENROLLMENT_EVENTS, groupId = "order-service")
  public void onEnrollmentCreated(EnrollmentCreatedEvent event) {
    log.info(
        "Consumed EnrollmentCreatedEvent eventId={} enrollmentId={}",
        event.eventId(),
        event.enrollmentId());
    orderService.createFromEnrollmentEvent(event);
  }
}
