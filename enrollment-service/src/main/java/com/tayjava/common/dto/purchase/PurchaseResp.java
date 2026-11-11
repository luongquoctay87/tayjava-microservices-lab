package com.tayjava.common.dto.purchase;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurchaseResp implements Serializable {
  private Long sagaId;
  private String status;
  private String currentStep;
  private Long enrollmentId;
  private String orderRef;
  private String paymentReceipt;
}
