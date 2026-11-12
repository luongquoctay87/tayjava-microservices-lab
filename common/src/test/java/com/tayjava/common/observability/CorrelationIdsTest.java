package com.tayjava.common.observability;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CorrelationIdsTest {

  @Test
  void acceptsSafeIds() {
    assertThat(CorrelationIds.isValid("checkout-abc_01")).isTrue();
    assertThat(CorrelationIds.isValid("550e8400-e29b-41d4-a716-446655440000")).isTrue();
  }

  @Test
  void rejectsUnsafeIds() {
    assertThat(CorrelationIds.isValid(null)).isFalse();
    assertThat(CorrelationIds.isValid("bad id with spaces")).isFalse();
    assertThat(CorrelationIds.isValid("x".repeat(65))).isFalse();
  }
}
