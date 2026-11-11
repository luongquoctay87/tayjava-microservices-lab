package com.tayjava.saga;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
@Component
@Slf4j(topic = "OrderIntentPort")
public class InMemoryOrderIntentPort implements OrderIntentPort {
  private final Map<String, String> orders = new ConcurrentHashMap<>();
  @Override public String createOrder(String sagaKey, String studentUsername, Long courseId) {
    String ref = "ord-" + sagaKey;
    orders.put(ref, "CREATED:" + studentUsername + ":" + courseId);
    log.info("Saga created orderRef={} student={} courseId={}", ref, studentUsername, courseId);
    return ref;
  }
  @Override public void cancelOrder(String orderRef) {
    if (orderRef != null) { orders.put(orderRef, "CANCELLED"); log.info("Saga compensated orderRef={}", orderRef); }
  }
  public String statusOf(String orderRef) { return orders.get(orderRef); }
}
