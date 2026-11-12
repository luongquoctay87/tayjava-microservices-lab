package com.tayjava.common.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderReq implements Serializable {

  @NotBlank private String studentUsername;
  @NotNull private Long courseId;
}
