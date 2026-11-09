package com.tayjava.common.dto.training.enrollment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tayjava.common.enums.EEnrollmentStatus;
import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnrollmentResp implements Serializable {

  private Long id;
  private String studentUsername;
  private Long courseId;
  private EEnrollmentStatus status;
}
