package com.tayjava.common.security;

/** Headers that must never be treated as proof of identity. */
public final class DemoAuthHeaders {

  public static final String USER_ID = "X-User-Id";
  public static final String ROLES = "X-Roles";

  private DemoAuthHeaders() {}
}
