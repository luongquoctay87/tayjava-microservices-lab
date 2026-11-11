package com.tayjava.controller;

import com.tayjava.common.dto.ApiResp;
import com.tayjava.common.dto.Resp;
import com.tayjava.common.dto.order.OrderResp;
import com.tayjava.service.OrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j(topic = "OrderController")
public class OrderController {

  private final OrderService orderService;

  @GetMapping
  public ResponseEntity<Resp<List<OrderResp>>> listByStudent(
      @RequestParam("student") String studentUsername) {
    return ApiResp.ok(orderService.getByStudentUsername(studentUsername));
  }
}
