package com.tayjava.saga;
public interface OrderIntentPort {
  String createOrder(String sagaKey, String studentUsername, Long courseId);
  void cancelOrder(String orderRef);
}
