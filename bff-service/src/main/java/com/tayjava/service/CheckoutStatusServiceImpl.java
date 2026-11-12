package com.tayjava.service;

import com.tayjava.client.OrderApiClient;
import com.tayjava.common.dto.bff.CheckoutStatusResp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CheckoutStatusServiceImpl implements CheckoutStatusService {

  private final OrderApiClient orderApiClient;

  @Override
  public CheckoutStatusResp getCheckoutStatus(Long orderId) {
    OrderApiClient.OrderItem order = orderApiClient.getById(orderId);
    return CheckoutStatusResp.builder()
        .orderId(order.getId())
        .enrollmentId(order.getEnrollmentId())
        .courseId(order.getCourseId())
        .status(order.getStatus())
        .message(messageFor(order.getStatus(), order.getId()))
        .build();
  }

  static String messageFor(String status, Long orderId) {
    if ("FULFILLING".equals(status)) {
      return "Order #" + orderId + " is being processed";
    }
    if ("FULFILLED".equals(status)) {
      return "Order #" + orderId + " is complete";
    }
    if ("REFUNDED".equals(status)) {
      return "Order #" + orderId + " was refunded";
    }
    return null;
  }
}
