package com.tayjava.metrics;

import com.tayjava.repository.CourseOrderRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class StuckFulfillingGaugeJob {

  private final CourseOrderRepository courseOrderRepository;
  private final CheckoutMetrics checkoutMetrics;

  @Scheduled(fixedDelayString = "${tayjava.metrics.stuck-poll-ms:60000}")
  public void refresh() {
    Instant cutoff = Instant.now().minus(5, ChronoUnit.MINUTES);
    long stuck =
        courseOrderRepository.findByStatus("FULFILLING").stream()
            .filter(
                o -> {
                  Instant paid = o.getPaidAt() != null ? o.getPaidAt() : o.getCreatedAt();
                  return paid != null && paid.isBefore(cutoff);
                })
            .count();
    checkoutMetrics.updateStuckOrders(stuck);
  }
}
