package com.tayjava.metrics;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class CheckoutMetricsTest {

  @Test
  void recordsFulfillSuccessTimer() {
    MeterRegistry registry = new SimpleMeterRegistry();
    CheckoutMetrics metrics = new CheckoutMetrics(registry);
    Instant start = Instant.parse("2026-01-01T00:00:00Z");
    metrics.recordFulfilled(start, start.plusSeconds(12));
    assertThat(registry.find("checkout.fulfill").tag("outcome", "success").timer().count())
        .isEqualTo(1);
  }

  @Test
  void updatesStuckGauge() {
    MeterRegistry registry = new SimpleMeterRegistry();
    CheckoutMetrics metrics = new CheckoutMetrics(registry);
    metrics.updateStuckOrders(3);
    assertThat(registry.find("checkout.fulfilling.stuck").gauge().value()).isEqualTo(3.0);
  }
}
