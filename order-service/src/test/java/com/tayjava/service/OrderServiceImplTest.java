package com.tayjava.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.tayjava.common.dto.event.EnrollmentCreatedEvent;
import com.tayjava.common.dto.order.OrderResp;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OrderServiceImplTest {

  @Autowired private OrderService orderService;

  @Test
  void createsOrderFromEnrollmentEventAndIsIdempotentByEnrollmentId() {
    EnrollmentCreatedEvent event =
        new EnrollmentCreatedEvent(
            "evt-1", 101L, "alice@example.com", 7L, "APPROVED", Instant.parse("2026-01-01T00:00:00Z"));

    OrderResp first = orderService.createFromEnrollmentEvent(event);
    OrderResp second =
        orderService.createFromEnrollmentEvent(
            new EnrollmentCreatedEvent(
                "evt-2", 101L, "alice@example.com", 7L, "APPROVED", Instant.now()));

    assertThat(first.getId()).isEqualTo(second.getId());
    assertThat(first.getEnrollmentId()).isEqualTo(101L);
    assertThat(first.getStatus()).isEqualTo("CREATED");

    List<OrderResp> listed = orderService.getByStudentUsername("alice@example.com");
    assertThat(listed).hasSize(1);
  }
}
