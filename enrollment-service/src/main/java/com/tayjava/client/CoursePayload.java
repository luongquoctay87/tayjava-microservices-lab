package com.tayjava.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
class CoursePayload {
  private Long id;
  private String slug;
  private String title;
  private Boolean published;
}
