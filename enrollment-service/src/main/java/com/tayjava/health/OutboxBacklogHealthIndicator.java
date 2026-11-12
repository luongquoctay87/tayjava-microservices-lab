package com.tayjava.health;

import com.tayjava.entity.OutboxEvent;
import com.tayjava.repository.OutboxEventRepository;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Custom readiness contributor: a huge PENDING outbox backlog means the service should stop taking
 * more work. Liveness stays independent of this indicator.
 */
@Component("outboxBacklog")
public class OutboxBacklogHealthIndicator implements HealthIndicator {

  private static final long MAX_PENDING = 10_000;

  private final OutboxEventRepository outboxEventRepository;

  public OutboxBacklogHealthIndicator(OutboxEventRepository outboxEventRepository) {
    this.outboxEventRepository = outboxEventRepository;
  }

  @Override
  public Health health() {
    long pending = outboxEventRepository.countByStatus(OutboxEvent.Status.PENDING);
    Health.Builder status = pending > MAX_PENDING ? Health.down() : Health.up();
    return status.withDetail("pending", pending).build();
  }
}
