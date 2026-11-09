package com.tayjava.common.dto.training.course;

import com.tayjava.common.ZConst;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseReq {

  @NotBlank(message = ZConst.MUST_NOT_BE_BLANK)
  private String slug;

  @NotBlank(message = ZConst.MUST_NOT_BE_BLANK)
  private String title;

  @NotBlank(message = ZConst.MUST_NOT_BE_BLANK)
  private String instructorUsername;
}
