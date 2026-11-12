package com.tayjava.service;

import com.tayjava.common.dto.event.EnrollmentCreatedEvent;
import com.tayjava.common.dto.order.OrderResp;
import java.util.List;

public interface OrderService {

  OrderResp createFromEnrollmentEvent(EnrollmentCreatedEvent event);

  List<OrderResp> getByStudentUsername(String studentUsername);

  OrderResp getById(Long orderId);

  OrderResp markPayInitiated(Long orderId);
}
