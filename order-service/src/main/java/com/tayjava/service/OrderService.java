package com.tayjava.service;

import com.tayjava.common.dto.event.EnrollmentCreatedEvent;
import com.tayjava.common.dto.order.CreateOrderReq;
import com.tayjava.common.dto.order.OrderResp;
import java.util.List;

public interface OrderService {

  OrderResp createCheckout(CreateOrderReq request);

  OrderResp createFromEnrollmentEvent(EnrollmentCreatedEvent event);

  List<OrderResp> getByStudentUsername(String studentUsername);

  List<OrderResp> listFulfillingByStudent(String studentUsername);

  OrderResp getById(Long orderId);

  OrderResp markPayInitiated(Long orderId);
}
