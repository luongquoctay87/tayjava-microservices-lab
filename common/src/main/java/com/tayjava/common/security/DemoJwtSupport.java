package com.tayjava.common.security;

/**
 * Shared constants for the lessons 28–29 security bridge. Deep JWT/OAuth2 belongs in the Spring
 * Security series; here services accept a lab-only Bearer token shape documented in the README.
 *
 * <p>Token format: {@code Authorization: Bearer demo:&lt;subject&gt;} optional {@code
 * ;scope=course.read}.
 */
public final class DemoJwtSupport {

  public static final String HEADER = "Authorization";
  public static final String BEARER_PREFIX = "Bearer ";
  public static final String DEMO_PREFIX = "demo:";
  public static final String SCOPE_COURSE_READ = "course.read";
  public static final String AUDIENCE_COURSE = "course-service";
  public static final String CLIENT_ENROLLMENT = "enrollment-service";

  private DemoJwtSupport() {}

  public static String userBearer(String username) {
    return BEARER_PREFIX + DEMO_PREFIX + username;
  }

  public static String serviceBearer(String clientId, String scope) {
    return BEARER_PREFIX + DEMO_PREFIX + clientId + ";scope=" + scope;
  }
}
