package com.tayjava.health;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.tayjava.entity.OutboxEvent;
import com.tayjava.repository.OutboxEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;

@ExtendWith(MockitoExtension.class)
class OutboxBacklogHealthIndicatorTest {

  @Mock private OutboxEventRepository outboxEventRepository;

  @Test
  void upWhenBacklogBelowThreshold() {
    when(outboxEventRepository.countByStatus(OutboxEvent.Status.PENDING)).thenReturn(3L);
    Health health = new OutboxBacklogHealthIndicator(outboxEventRepository).health();
    assertThat(health.getStatus()).isEqualTo(Status.UP);
    assertThat(health.getDetails()).containsEntry("pending", 3L);
  }

  @Test
  void downWhenBacklogHuge() {
    when(outboxEventRepository.countByStatus(OutboxEvent.Status.PENDING)).thenReturn(10_001L);
    Health health = new OutboxBacklogHealthIndicator(outboxEventRepository).health();
    assertThat(health.getStatus()).isEqualTo(Status.DOWN);
  }
}
