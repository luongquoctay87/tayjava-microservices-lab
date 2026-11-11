package com.tayjava.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Demo payment side-effect keyed by the same Idempotency-Key as enroll. Not a real payment
 * provider — only shows that retries must not charge twice.
 */
@Component
@Slf4j(topic = "PaymentStub")
public class PaymentStub {

  private final Map<String, String> chargesByKey = new ConcurrentHashMap<>();

  public String chargeOnce(String idempotencyKey, String studentUsername, Long courseId) {
    return chargesByKey.computeIfAbsent(
        idempotencyKey,
        key -> {
          String receipt = "pay-" + key;
          log.info(
              "Payment stub charged once key={} student={} courseId={} receipt={}",
              key,
              studentUsername,
              courseId,
              receipt);
          return receipt;
        });
  }

  public int chargedKeys() {
    return chargesByKey.size();
  }
  public void refund(String idempotencyKey) {
    if (chargesByKey.remove(idempotencyKey) != null) {
      log.info("Payment stub refunded key={}", idempotencyKey);
    }
  }
  public boolean wasCharged(String idempotencyKey) {
    return chargesByKey.containsKey(idempotencyKey);
  }
}
