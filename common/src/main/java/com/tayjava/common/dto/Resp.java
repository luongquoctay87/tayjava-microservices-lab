package com.tayjava.common.dto;

import com.tayjava.common.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Standard API envelope: success flag, machine-oriented {@code code}, human-readable {@code
 * message}, and optional typed {@code data}.
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Resp<T> {

  private boolean success;
  private String code;
  private String message;
  private T data;

  public static <T> Resp<T> error(ErrorCode code, String message) {
    return Resp.<T>builder().success(false).code(code.name()).message(message).build();
  }

  public static <T> Resp<T> error(ErrorCode errorCode) {
    return error(errorCode, errorCode.getMessage());
  }
}
