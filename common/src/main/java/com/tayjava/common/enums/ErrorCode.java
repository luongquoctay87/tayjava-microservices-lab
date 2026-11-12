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
  UNSUPPORTED_API_VERSION("Unsupported API version", HttpStatus.BAD_REQUEST),
  UNAUTHORIZED("Authentication required", HttpStatus.UNAUTHORIZED),
  FORBIDDEN("Insufficient privileges", HttpStatus.FORBIDDEN),
  DATA_NOT_FOUND("Data not found", HttpStatus.NOT_FOUND),
  COURSE_NOT_FOUND("Course not found", HttpStatus.NOT_FOUND),
  COURSE_NOT_ENROLLABLE("Course cannot be enrolled", HttpStatus.UNPROCESSABLE_CONTENT),
  COURSE_UNAVAILABLE("Course service unavailable", HttpStatus.SERVICE_UNAVAILABLE),
  BAD_GATEWAY("Unexpected response from an upstream service", HttpStatus.BAD_GATEWAY),
  IDEMPOTENCY_KEY_REQUIRED("Idempotency-Key header is required", HttpStatus.BAD_REQUEST),
  IDEMPOTENCY_KEY_CONFLICT("Idempotency key reused with a different payload", HttpStatus.CONFLICT),
  SAGA_FAILED("Purchase saga failed", HttpStatus.UNPROCESSABLE_CONTENT),
  PAYMENT_DECLINED("Payment declined", HttpStatus.UNPROCESSABLE_CONTENT),

  INTERNAL_SERVER_ERROR("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String message;
  private final HttpStatus httpStatus;
}
