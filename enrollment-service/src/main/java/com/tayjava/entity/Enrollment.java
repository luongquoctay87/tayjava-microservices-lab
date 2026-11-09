package com.tayjava.entity;

import com.tayjava.common.enums.EEnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Enrollment owned by enrollment-service. Stores {@code courseId} only — the course title stays in
 * course-service.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {

  private Long id;
  private String studentUsername;
  private Long courseId;
  private EEnrollmentStatus enrollStatus;
}
