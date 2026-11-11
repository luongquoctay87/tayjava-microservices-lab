package com.tayjava.controller;

import com.tayjava.common.dto.ApiResp;
import com.tayjava.common.dto.Resp;
import com.tayjava.common.dto.purchase.PurchaseReq;
import com.tayjava.common.dto.purchase.PurchaseResp;
import com.tayjava.saga.PurchaseSagaOrchestrator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/purchases")
@RequiredArgsConstructor
@Slf4j(topic = "PurchaseController")
public class PurchaseController {
  private final PurchaseSagaOrchestrator purchaseSagaOrchestrator;
  @PostMapping
  public ResponseEntity<Resp<PurchaseResp>> purchase(
      @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
      @Valid @RequestBody PurchaseReq request) {
    return ApiResp.created(purchaseSagaOrchestrator.purchase(request, idempotencyKey));
  }
}
