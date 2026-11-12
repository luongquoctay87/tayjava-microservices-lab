package com.tayjava.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.tayjava.common.dto.event.EnrollmentCreatedEvent;
import com.tayjava.common.dto.order.CreateOrderReq;
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
            "evt-1",
            101L,
            "alice@example.com",
            7L,
            "APPROVED",
            Instant.parse("2026-01-01T00:00:00Z"),
            null,
            null);

    OrderResp first = orderService.createFromEnrollmentEvent(event);
    OrderResp second =
        orderService.createFromEnrollmentEvent(
            new EnrollmentCreatedEvent(
                "evt-2", 101L, "alice@example.com", 7L, "APPROVED", Instant.now(), null, null));

    assertThat(first.getId()).isEqualTo(second.getId());
    assertThat(first.getEnrollmentId()).isEqualTo(101L);
    assertThat(first.getStatus()).isEqualTo("CREATED");

    List<OrderResp> listed = orderService.getByStudentUsername("alice@example.com");
    assertThat(listed).hasSize(1);
  }

  @Test
  void payReturnsFulfillingThenEnrollmentCreatedMarksFulfilled() {
    CreateOrderReq req = new CreateOrderReq();
    req.setStudentUsername("carol@example.com");
    req.setCourseId(42L);

    OrderResp created = orderService.createCheckout(req);
    assertThat(created.getEnrollmentId()).isNull();
    assertThat(created.getStatus()).isEqualTo("CREATED");

    OrderResp paying = orderService.markPayInitiated(created.getId());
    assertThat(paying.getStatus()).isEqualTo("FULFILLING");
    assertThat(paying.getEnrollmentId()).isNull();

    OrderResp again = orderService.markPayInitiated(created.getId());
    assertThat(again.getStatus()).isEqualTo("FULFILLING");

    OrderResp fulfilled =
        orderService.createFromEnrollmentEvent(
            new EnrollmentCreatedEvent(
                "evt-fulfill-1",
                9001L,
                "carol@example.com",
                42L,
                "APPROVED",
                Instant.now(),
                null,
                null));

    assertThat(fulfilled.getId()).isEqualTo(created.getId());
    assertThat(fulfilled.getStatus()).isEqualTo("FULFILLED");
    assertThat(fulfilled.getEnrollmentId()).isEqualTo(9001L);
    assertThat(orderService.listFulfillingByStudent("carol@example.com")).isEmpty();
  }
}
