package com.tayjava.messaging;

import static org.assertj.core.api.Assertions.assertThat;

import com.tayjava.common.dto.training.enrollment.EnrollmentResp;
import com.tayjava.common.enums.EEnrollmentStatus;
import com.tayjava.entity.OutboxEvent;
import com.tayjava.repository.OutboxEventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OutboxRelayTest {

  @Autowired private OutboxAppender outboxAppender;
  @Autowired private OutboxRelay outboxRelay;
  @Autowired private OutboxEventRepository outboxEventRepository;
  @Autowired private OutboxRelay.OutboxKafkaSender outboxKafkaSender;

  @Test
  void appendThenRelayMarksPublishedAndSendsPayload() {
    outboxAppender.appendEnrollmentCreated(
        EnrollmentResp.builder()
            .id(77L)
            .studentUsername("alice@example.com")
            .courseId(3L)
            .status(EEnrollmentStatus.APPROVED)
            .build());

    assertThat(outboxEventRepository.findTop50ByStatusOrderByIdAsc(OutboxEvent.Status.PENDING))
        .isNotEmpty();

    int published = outboxRelay.publishPending();

    assertThat(published).isGreaterThanOrEqualTo(1);
    assertThat(outboxEventRepository.findTop50ByStatusOrderByIdAsc(OutboxEvent.Status.PENDING))
        .isEmpty();
    assertThat(outboxKafkaSender.lastSent()).isNotNull();
    assertThat(outboxKafkaSender.lastSent().enrollmentId()).isEqualTo(77L);
    assertThat(outboxKafkaSender.lastSent().studentUsername()).isEqualTo("alice@example.com");
  }
}
