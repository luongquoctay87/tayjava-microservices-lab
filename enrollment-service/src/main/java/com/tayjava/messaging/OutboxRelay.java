package com.tayjava.messaging;

import tools.jackson.databind.ObjectMapper;
import com.tayjava.common.dto.event.EnrollmentCreatedEvent;
import com.tayjava.common.messaging.KafkaTopics;
import com.tayjava.entity.OutboxEvent;
import com.tayjava.repository.OutboxEventRepository;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Polls PENDING outbox rows and publishes them to Kafka (or a recording sender in tests).
 */
@Component
@RequiredArgsConstructor
@Slf4j(topic = "OutboxRelay")
public class OutboxRelay {

  private final OutboxEventRepository outboxEventRepository;
  private final ObjectMapper objectMapper;
  private final OutboxKafkaSender outboxKafkaSender;

  @Scheduled(fixedDelayString = "${tayjava.outbox.poll-ms:1000}")
  public void scheduledPublish() {
    publishPending();
  }

  @Transactional
  public int publishPending() {
    List<OutboxEvent> pending =
        outboxEventRepository.findTop50ByStatusOrderByIdAsc(OutboxEvent.Status.PENDING);
    int published = 0;
    for (OutboxEvent row : pending) {
      try {
        EnrollmentCreatedEvent event =
            objectMapper.readValue(row.getPayload(), EnrollmentCreatedEvent.class);
        outboxKafkaSender.send(KafkaTopics.ENROLLMENT_EVENTS, row.getAggregateId(), event);
        row.setStatus(OutboxEvent.Status.PUBLISHED);
        row.setPublishedAt(Instant.now());
        outboxEventRepository.save(row);
        published++;
        log.info(
            "Outbox published eventId={} aggregateId={}", row.getEventId(), row.getAggregateId());
      } catch (Exception ex) {
        log.error("Outbox publish failed id={} eventId={}", row.getId(), row.getEventId(), ex);
      }
    }
    return published;
  }

  public interface OutboxKafkaSender {
    void send(String topic, String key, EnrollmentCreatedEvent event);

    default EnrollmentCreatedEvent lastSent() {
      return null;
    }
  }

  @Component
  @ConditionalOnProperty(name = "tayjava.kafka.enabled", havingValue = "true", matchIfMissing = true)
  @RequiredArgsConstructor
  static class KafkaOutboxSender implements OutboxKafkaSender {
    private final KafkaTemplate<String, EnrollmentCreatedEvent> kafkaTemplate;

    @Override
    public void send(String topic, String key, EnrollmentCreatedEvent event) {
      kafkaTemplate.send(topic, key, event);
    }
  }

  @Component
  @ConditionalOnProperty(name = "tayjava.kafka.enabled", havingValue = "false")
  static class RecordingOutboxSender implements OutboxKafkaSender {
    private final AtomicReference<EnrollmentCreatedEvent> last = new AtomicReference<>();

    @Override
    public void send(String topic, String key, EnrollmentCreatedEvent event) {
      last.set(event);
    }

    @Override
    public EnrollmentCreatedEvent lastSent() {
      return last.get();
    }
  }
}
