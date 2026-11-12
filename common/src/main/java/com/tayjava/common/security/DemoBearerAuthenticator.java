package com.tayjava.common.security;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/** Parses lab-only {@code Bearer demo:...} tokens. Not for production. */
public final class DemoBearerAuthenticator {

  public record DemoPrincipal(String subject, Set<String> scopes) {}

  private DemoBearerAuthenticator() {}

  public static Optional<DemoPrincipal> authenticate(String authorizationHeader) {
    if (authorizationHeader == null || authorizationHeader.isBlank()) {
      return Optional.empty();
    }
    String value = authorizationHeader.trim();
    if (!value.regionMatches(
        true, 0, DemoJwtSupport.BEARER_PREFIX, 0, DemoJwtSupport.BEARER_PREFIX.length())) {
      return Optional.empty();
    }
    String token = value.substring(DemoJwtSupport.BEARER_PREFIX.length()).trim();
    if (!token.startsWith(DemoJwtSupport.DEMO_PREFIX)) {
      return Optional.empty();
    }
    String body = token.substring(DemoJwtSupport.DEMO_PREFIX.length());
    String subject = body;
    Set<String> scopes = Set.of();
    int scopeIdx = body.indexOf(";scope=");
    if (scopeIdx >= 0) {
      subject = body.substring(0, scopeIdx);
      String scopePart = body.substring(scopeIdx + ";scope=".length());
      scopes =
          Arrays.stream(scopePart.split("[\\s,]"))
              .map(String::trim)
              .filter(s -> !s.isEmpty())
              .map(s -> s.toLowerCase(Locale.ROOT))
              .collect(Collectors.toUnmodifiableSet());
    }
    if (subject.isBlank()) {
      return Optional.empty();
    }
    return Optional.of(new DemoPrincipal(subject, scopes));
  }
}
