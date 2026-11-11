package com.tayjava.common.dto;

import com.tayjava.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/** Factory for {@link ResponseEntity} wrapping {@link Resp}. */
public final class ApiResp {

  private ApiResp() {}

  public static <T> ResponseEntity<Resp<T>> ok(T data) {
    return ResponseEntity.ok(
        Resp.<T>builder()
            .success(true)
            .code("SUCCESS")
            .data(data)
            .message("Data retrieved successfully")
            .build());
  }

  public static <T> ResponseEntity<Resp<T>> created(T data) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            Resp.<T>builder()
                .success(true)
                .code("SUCCESS")
                .message("Data saved successfully")
                .data(data)
                .build());
  }

  public static <T> ResponseEntity<Resp<T>> error(ErrorCode code, String message) {
    return ResponseEntity.status(code.getHttpStatus())
        .body(Resp.<T>builder().success(false).code(code.name()).message(message).build());
  }
}
