package com.tayjava.common.dto.training.course;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseResp implements Serializable {

  private Long id;
  private String slug;
  private String title;
  private String instructorUsername;
  private Boolean published;
}
