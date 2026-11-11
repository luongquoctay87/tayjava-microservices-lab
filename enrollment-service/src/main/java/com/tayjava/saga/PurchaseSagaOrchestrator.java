package com.tayjava.saga;

import com.tayjava.client.CourseClient;
import com.tayjava.common.dto.purchase.PurchaseReq;
import com.tayjava.common.dto.purchase.PurchaseResp;
import com.tayjava.common.dto.training.enrollment.EnrollmentReq;
import com.tayjava.common.dto.training.enrollment.EnrollmentResp;
import com.tayjava.common.enums.ErrorCode;
import com.tayjava.common.exception.ErrorException;
import com.tayjava.entity.SagaInstance;
import com.tayjava.repository.SagaInstanceRepository;
import com.tayjava.service.EnrollmentWriter;
import com.tayjava.service.PaymentStub;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "PurchaseSaga")
public class PurchaseSagaOrchestrator {
  static final String PURCHASE_SCOPE = "PURCHASE";
  private final SagaInstanceRepository sagaInstanceRepository;
  private final CourseClient courseClient;
  private final OrderIntentPort orderIntentPort;
  private final PaymentStub paymentStub;
  private final EnrollmentWriter enrollmentWriter;

  @Transactional
  public PurchaseResp purchase(PurchaseReq request, String idempotencyKey) {
    if (!StringUtils.hasText(idempotencyKey)) {
      throw new ErrorException(ErrorCode.IDEMPOTENCY_KEY_REQUIRED, "Idempotency-Key is required");
    }
    String key = idempotencyKey.trim();
    return sagaInstanceRepository.findByIdempotencyKey(key).map(this::toResp).orElseGet(() -> runNewSaga(request, key));
  }

  private PurchaseResp runNewSaga(PurchaseReq request, String key) {
    Instant now = Instant.now();
    SagaInstance saga = sagaInstanceRepository.save(SagaInstance.builder()
        .idempotencyKey(key).studentUsername(request.getStudentUsername()).studentEmail(request.getStudentEmail())
        .courseId(request.getCourseId()).status(SagaInstance.Status.RUNNING).currentStep(SagaInstance.Step.VALIDATE_COURSE)
        .createdAt(now).updatedAt(now).build());
    List<SagaInstance.Step> completed = new ArrayList<>();
    try {
      courseClient.requirePublished(request.getCourseId());
      completed.add(SagaInstance.Step.VALIDATE_COURSE);
      advance(saga, SagaInstance.Step.CREATE_ORDER);
      String orderRef = orderIntentPort.createOrder(key, request.getStudentUsername(), request.getCourseId());
      saga.setOrderRef(orderRef);
      completed.add(SagaInstance.Step.CREATE_ORDER);
      advance(saga, SagaInstance.Step.CHARGE_PAYMENT);
      if ("bob@example.com".equalsIgnoreCase(request.getStudentEmail())) {
        throw new ErrorException(ErrorCode.PAYMENT_DECLINED, "Payment declined for demo user");
      }
      String receipt = paymentStub.chargeOnce(key, request.getStudentUsername(), request.getCourseId());
      saga.setPaymentReceipt(receipt);
      completed.add(SagaInstance.Step.CHARGE_PAYMENT);
      advance(saga, SagaInstance.Step.CONFIRM_ENROLLMENT);
      EnrollmentReq enrollReq = new EnrollmentReq();
      enrollReq.setStudentUsername(request.getStudentUsername());
      enrollReq.setCourseId(request.getCourseId());
      EnrollmentResp enrollment = enrollmentWriter.persistNew(enrollReq, PURCHASE_SCOPE, key, "purchase|" + key);
      saga.setEnrollmentId(enrollment.getId());
      completed.add(SagaInstance.Step.CONFIRM_ENROLLMENT);
      saga.setStatus(SagaInstance.Status.COMPLETED);
      saga.setCurrentStep(SagaInstance.Step.DONE);
      saga.setUpdatedAt(Instant.now());
      return toResp(sagaInstanceRepository.save(saga));
    } catch (RuntimeException ex) {
      compensate(saga, completed, ex);
      throw ex instanceof ErrorException ? (ErrorException) ex : new ErrorException(ErrorCode.SAGA_FAILED, ex.getMessage());
    }
  }

  private void compensate(SagaInstance saga, List<SagaInstance.Step> completed, RuntimeException ex) {
    saga.setStatus(SagaInstance.Status.COMPENSATING);
    saga.setFailureReason(ex.getMessage());
    saga.setUpdatedAt(Instant.now());
    sagaInstanceRepository.save(saga);
    for (int i = completed.size() - 1; i >= 0; i--) {
      switch (completed.get(i)) {
        case CONFIRM_ENROLLMENT -> log.warn("Enrollment compensation is manual in this lab sagaId={} enrollmentId={}", saga.getId(), saga.getEnrollmentId());
        case CHARGE_PAYMENT -> paymentStub.refund(saga.getIdempotencyKey());
        case CREATE_ORDER -> orderIntentPort.cancelOrder(saga.getOrderRef());
        case VALIDATE_COURSE, DONE -> {}
      }
    }
    saga.setStatus(SagaInstance.Status.COMPENSATED);
    saga.setUpdatedAt(Instant.now());
    sagaInstanceRepository.save(saga);
    log.warn("Saga compensated id={} reason={}", saga.getId(), ex.getMessage());
  }

  private void advance(SagaInstance saga, SagaInstance.Step next) {
    saga.setCurrentStep(next);
    saga.setUpdatedAt(Instant.now());
    sagaInstanceRepository.save(saga);
  }

  private PurchaseResp toResp(SagaInstance saga) {
    return PurchaseResp.builder().sagaId(saga.getId()).status(saga.getStatus().name()).currentStep(saga.getCurrentStep().name())
        .enrollmentId(saga.getEnrollmentId()).orderRef(saga.getOrderRef()).paymentReceipt(saga.getPaymentReceipt()).build();
  }
}
