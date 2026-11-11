package com.tayjava.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Enrollment lifecycle status for course access. */
@Getter
@AllArgsConstructor
public enum EEnrollmentStatus {

  PENDING("Pending"),
  TRIAL("Trial"),
  APPROVED("Active / Approved"),
  EXPIRED("Expired"),
  REJECTED("Rejected"),
  CANCELED("Canceled");

  private final String value;
}
