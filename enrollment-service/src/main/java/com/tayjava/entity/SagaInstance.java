package com.tayjava.entity;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

@Entity
@Table(name = "saga_instances")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class SagaInstance {
  public enum Status { RUNNING, COMPLETED, COMPENSATING, COMPENSATED, FAILED }
  public enum Step { VALIDATE_COURSE, CREATE_ORDER, CHARGE_PAYMENT, CONFIRM_ENROLLMENT, DONE }
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @Column(name = "idempotency_key", nullable = false, unique = true, length = 100) private String idempotencyKey;
  @Column(name = "student_username", nullable = false, length = 100) private String studentUsername;
  @Column(name = "student_email", nullable = false, length = 200) private String studentEmail;
  @Column(name = "course_id", nullable = false) private Long courseId;
  @Enumerated(EnumType.STRING) @Column(name = "status", nullable = false, length = 20) private Status status;
  @Enumerated(EnumType.STRING) @Column(name = "current_step", nullable = false, length = 40) private Step currentStep;
  @Column(name = "order_ref", length = 64) private String orderRef;
  @Column(name = "payment_receipt", length = 64) private String paymentReceipt;
  @Column(name = "enrollment_id") private Long enrollmentId;
  @Column(name = "failure_reason", length = 500) private String failureReason;
  @Column(name = "created_at", nullable = false) private Instant createdAt;
  @Column(name = "updated_at", nullable = false) private Instant updatedAt;
}
