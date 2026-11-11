package com.tayjava.saga;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import com.tayjava.client.CourseClient;
import com.tayjava.common.dto.purchase.PurchaseReq;
import com.tayjava.common.dto.purchase.PurchaseResp;
import com.tayjava.common.enums.ErrorCode;
import com.tayjava.common.exception.ErrorException;
import com.tayjava.service.PaymentStub;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class PurchaseSagaOrchestratorTest {
  @Autowired private PurchaseSagaOrchestrator orchestrator;
  @Autowired private PaymentStub paymentStub;
  @Autowired private InMemoryOrderIntentPort orderIntentPort;
  @MockitoBean private CourseClient courseClient;

  @Test
  void happyPathCompletesSagaAndChargesOnce() {
    PurchaseReq req = new PurchaseReq();
    req.setStudentUsername("an.student");
    req.setStudentEmail("alice@example.com");
    req.setCourseId(1L);
    PurchaseResp resp = orchestrator.purchase(req, "saga-alice-1");
    assertThat(resp.getStatus()).isEqualTo("COMPLETED");
    assertThat(resp.getEnrollmentId()).isNotNull();
    assertThat(resp.getOrderRef()).startsWith("ord-");
    assertThat(paymentStub.wasCharged("saga-alice-1")).isTrue();
    verify(courseClient).requirePublished(1L);
  }

  @Test
  void paymentDeclineCompensatesOrderAndDoesNotLeaveCharge() {
    PurchaseReq req = new PurchaseReq();
    req.setStudentUsername("bob.student");
    req.setStudentEmail("bob@example.com");
    req.setCourseId(2L);
    assertThatThrownBy(() -> orchestrator.purchase(req, "saga-bob-decline"))
        .isInstanceOf(ErrorException.class)
        .extracting(ex -> ((ErrorException) ex).getErrorCode())
        .isEqualTo(ErrorCode.PAYMENT_DECLINED);
    assertThat(paymentStub.wasCharged("saga-bob-decline")).isFalse();
    assertThat(orderIntentPort.statusOf("ord-saga-bob-decline")).isEqualTo("CANCELLED");
  }
}
