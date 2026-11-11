package com.tayjava.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/** Canonical API error codes mapped to HTTP status values. */
@Getter
@AllArgsConstructor
public enum ErrorCode {

  SUCCESS("Success", HttpStatus.OK),

  INVALID_REQUEST("Invalid request", HttpStatus.BAD_REQUEST),
  VALIDATION_ERROR("Validation failed", HttpStatus.BAD_REQUEST),
  DATA_NOT_FOUND("Data not found", HttpStatus.NOT_FOUND),
  COURSE_NOT_FOUND("Course not found", HttpStatus.NOT_FOUND),

  INTERNAL_SERVER_ERROR("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String message;
  private final HttpStatus httpStatus;
}
