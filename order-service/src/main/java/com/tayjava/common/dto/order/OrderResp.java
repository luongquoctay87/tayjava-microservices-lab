package com.tayjava.common.dto.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResp implements Serializable {

  private Long id;
  private Long enrollmentId;
  private String studentUsername;
  private Long courseId;
  private String status;
  private String sourceEventId;
  private Instant createdAt;
}
