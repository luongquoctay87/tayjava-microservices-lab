package com.tayjava.common.dto.purchase;

import com.tayjava.common.ZConst;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseReq {
  @NotBlank(message = ZConst.MUST_NOT_BE_BLANK)
  private String studentUsername;
  @NotBlank(message = ZConst.MUST_NOT_BE_BLANK)
  @Email
  private String studentEmail;
  @NotNull(message = ZConst.MUST_NOT_BE_NULL)
  @Min(value = 1, message = ZConst.MUST_BE_GREATER_THAN_0)
  private Long courseId;
}
