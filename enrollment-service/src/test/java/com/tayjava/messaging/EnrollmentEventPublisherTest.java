package com.tayjava.messaging;

import static org.assertj.core.api.Assertions.assertThat;

import com.tayjava.common.dto.training.enrollment.EnrollmentResp;
import com.tayjava.common.enums.EEnrollmentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EnrollmentEventPublisherTest {

  @Autowired private EnrollmentEventPublisher enrollmentEventPublisher;

  @Test
  void noOpPublisherRecordsLastEventWhenKafkaDisabled() {
    EnrollmentResp enrollment =
        EnrollmentResp.builder()
            .id(42L)
            .studentUsername("alice@example.com")
            .courseId(9L)
            .status(EEnrollmentStatus.APPROVED)
            .build();

    enrollmentEventPublisher.publishEnrollmentCreated(enrollment);

    assertThat(enrollmentEventPublisher.lastPublished()).isNotNull();
    assertThat(enrollmentEventPublisher.lastPublished().enrollmentId()).isEqualTo(42L);
    assertThat(enrollmentEventPublisher.lastPublished().studentUsername())
        .isEqualTo("alice@example.com");
  }
}
