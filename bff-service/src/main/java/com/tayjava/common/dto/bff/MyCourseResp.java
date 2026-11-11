package com.tayjava.common.dto.bff;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tayjava.common.enums.EEnrollmentStatus;
import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MyCourseResp implements Serializable {

  private Long enrollmentId;
  private Long courseId;
  private String slug;
  private String title;
  private String instructorUsername;
  private EEnrollmentStatus status;
}
