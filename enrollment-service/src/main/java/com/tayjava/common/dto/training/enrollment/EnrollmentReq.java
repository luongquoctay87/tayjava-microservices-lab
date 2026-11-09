package com.tayjava.common.dto.training.enrollment;

import com.tayjava.common.ZConst;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnrollmentReq {

  @NotBlank(message = ZConst.MUST_NOT_BE_BLANK)
  private String studentUsername;

  @NotNull(message = ZConst.MUST_NOT_BE_NULL)
  @Min(value = 1, message = ZConst.MUST_BE_GREATER_THAN_0)
  private Long courseId;
}
