package com.tayjava.common.exception;

import com.tayjava.common.enums.ErrorCode;
import lombok.Getter;

/**
 * Domain failure mapped to a stable {@link ErrorCode} and HTTP status via {@link
 * GlobalExceptionHandling}.
 */
@Getter
public class ErrorException extends RuntimeException {

  private final ErrorCode errorCode;

  public ErrorException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public ErrorException(ErrorCode errorCode, String errorMessage) {
    super(errorMessage);
    this.errorCode = errorCode;
  }
}
