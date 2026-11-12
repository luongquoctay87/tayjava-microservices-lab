package com.tayjava.controller;

import com.tayjava.common.dto.ApiResp;
import com.tayjava.common.dto.Resp;
import com.tayjava.common.dto.bff.CheckoutStatusResp;
import com.tayjava.service.CheckoutStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/checkout-status")
public class CheckoutStatusController {

  private final CheckoutStatusService checkoutStatusService;

  @GetMapping("/{orderId}")
  public ResponseEntity<Resp<CheckoutStatusResp>> checkoutStatus(
      @PathVariable("orderId") Long orderId) {
    return ApiResp.ok(checkoutStatusService.getCheckoutStatus(orderId));
  }
}
