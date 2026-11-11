package com.tayjava.messaging;

import static org.assertj.core.api.Assertions.assertThat;

import com.tayjava.common.dto.event.EnrollmentCreatedEvent;
import com.tayjava.common.dto.order.OrderResp;
import com.tayjava.repository.CourseOrderRepository;
import com.tayjava.service.OrderService;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class InboxGuardTest {

  @Autowired private OrderService orderService;
  @Autowired private CourseOrderRepository courseOrderRepository;
  @Autowired private InboxGuard inboxGuard;

  @Test
  void duplicateEventIdIsSkippedByInbox() {
    EnrollmentCreatedEvent event =
        new EnrollmentCreatedEvent(
            "inbox-evt-1", 201L, "bob@example.com", 5L, "APPROVED", Instant.now());

    OrderResp first = orderService.createFromEnrollmentEvent(event);
    OrderResp second = orderService.createFromEnrollmentEvent(event);

    assertThat(first).isNotNull();
    assertThat(second).isNotNull();
    assertThat(first.getId()).isEqualTo(second.getId());
    assertThat(courseOrderRepository.findByEnrollmentId(201L)).isPresent();
    assertThat(courseOrderRepository.findAll().stream().filter(o -> o.getEnrollmentId().equals(201L)))
        .hasSize(1);
    assertThat(inboxGuard.claim("inbox-evt-1", "EnrollmentCreated")).isFalse();
  }
}
