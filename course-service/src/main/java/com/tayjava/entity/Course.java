package com.tayjava.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Course owned by course-service. In-memory until lesson 7. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {

  private Long id;
  private String slug;
  private String title;
  private String instructorUsername;
  private boolean published;
}
