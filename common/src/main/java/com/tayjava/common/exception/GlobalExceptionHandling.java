package com.tayjava.common.exception;

import com.tayjava.common.dto.Resp;
import com.tayjava.common.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Maps application and validation failures to the {@link Resp} envelope. */
@RestControllerAdvice
@Slf4j(topic = "GLOBAL-EXCEPTION")
public class GlobalExceptionHandling {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Resp<Void>> handleValidation(MethodArgumentNotValidException ex) {
    String message =
        ex.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(err -> err.getField() + " " + err.getDefaultMessage())
            .orElse("Invalid payload");
    log.warn("Validation error: {}", message);
    return ResponseEntity.status(ErrorCode.VALIDATION_ERROR.getHttpStatus())
        .body(Resp.error(ErrorCode.VALIDATION_ERROR, message));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Resp<Void>> handleUnreadable(HttpMessageNotReadableException ex) {
    log.warn("Malformed JSON: {}", ex.getMessage());
    return ResponseEntity.status(ErrorCode.INVALID_REQUEST.getHttpStatus())
        .body(Resp.error(ErrorCode.INVALID_REQUEST, "Malformed JSON or invalid request body"));
  }

  @ExceptionHandler(ErrorException.class)
  public ResponseEntity<Resp<Void>> handleBusiness(ErrorException ex) {
    return ResponseEntity.status(ex.getErrorCode().getHttpStatus())
        .body(Resp.error(ex.getErrorCode(), ex.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Resp<Void>> handleUnknown(Exception ex) {
    log.error("Unhandled exception", ex);
    return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
        .body(Resp.error(ErrorCode.INTERNAL_SERVER_ERROR, "Unexpected error occurred"));
  }
}
