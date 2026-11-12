package com.tayjava.common.dto.bff;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CheckoutStatusResp implements Serializable {

  private Long orderId;
  private Long enrollmentId;
  private Long courseId;
  private String status;
  private String message;
}
