package com.tayjava.common.observability;

import java.util.regex.Pattern;

/** Shared correlation-id header / MDC keys (Gateway → services → Kafka). */
public final class CorrelationIds {

  public static final String HEADER = "X-Correlation-Id";
  public static final String MDC_KEY = "correlationId";
  private static final Pattern VALID = Pattern.compile("[A-Za-z0-9._-]{1,64}");

  private CorrelationIds() {}

  public static boolean isValid(String value) {
    return value != null && VALID.matcher(value).matches();
  }
}
