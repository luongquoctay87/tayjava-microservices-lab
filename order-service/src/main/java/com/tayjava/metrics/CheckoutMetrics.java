package com.tayjava.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

@Component
public class CheckoutMetrics {

  private final Timer fulfilled;
  private final Timer failed;
  private final AtomicLong stuckOrders = new AtomicLong();

  public CheckoutMetrics(MeterRegistry registry) {
    this.fulfilled = fulfillTimer(registry, "success");
    this.failed = fulfillTimer(registry, "failure");
    Gauge.builder("checkout.fulfilling.stuck", stuckOrders, AtomicLong::get)
        .description("Orders in FULFILLING for more than 5 minutes")
        .register(registry);
  }

  private static Timer fulfillTimer(MeterRegistry registry, String outcome) {
    return Timer.builder("checkout.fulfill")
        .description("Time from PAID until the order finishes fulfillment")
        .tag("outcome", outcome)
        .serviceLevelObjectives(Duration.ofSeconds(30))
        .publishPercentileHistogram()
        .minimumExpectedValue(Duration.ofMillis(100))
        .maximumExpectedValue(Duration.ofMinutes(10))
        .register(registry);
  }

  public void recordFulfilled(Instant paidAt, Instant enrolledAt) {
    if (paidAt == null || enrolledAt == null || enrolledAt.isBefore(paidAt)) {
      return;
    }
    fulfilled.record(Duration.between(paidAt, enrolledAt));
  }

  public void recordFailed(Instant paidAt, Instant failedAt) {
    if (paidAt == null || failedAt == null || failedAt.isBefore(paidAt)) {
      return;
    }
    failed.record(Duration.between(paidAt, failedAt));
  }

  public void updateStuckOrders(long count) {
    stuckOrders.set(count);
  }
}
