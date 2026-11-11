package com.tayjava.gateway;

import java.time.Duration;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Thin edge aggregate: course count + enrollment health. Not a BFF — My Courses stays in
 * bff-service.
 */
@RestController
public class CatalogSummaryController {

  private static final Duration CALL_TIMEOUT = Duration.ofSeconds(1);

  private final WebClient courseClient;
  private final WebClient enrollmentClient;

  public CatalogSummaryController(
      WebClient.Builder builder,
      @Value("${COURSE_URI:http://localhost:8081}") String courseUri,
      @Value("${ENROLLMENT_URI:http://localhost:8082}") String enrollmentUri) {
    this.courseClient = builder.clone().baseUrl(courseUri).build();
    this.enrollmentClient = builder.clone().baseUrl(enrollmentUri).build();
  }

  @GetMapping("/api/catalog/summary")
  public Mono<CatalogSummary> summary() {
    Mono<Optional<Long>> courseCount =
        courseClient
            .get()
            .uri("/api/v1/courses")
            .retrieve()
            .bodyToMono(CourseListEnvelope.class)
            .map(body -> Optional.of(body == null || body.data() == null ? 0L : (long) body.data().size()))
            .timeout(CALL_TIMEOUT)
            .onErrorReturn(Optional.empty());

    Mono<String> enrollmentStatus =
        enrollmentClient
            .get()
            .uri("/actuator/health")
            .retrieve()
            .bodyToMono(HealthStatus.class)
            .map(HealthStatus::status)
            .timeout(CALL_TIMEOUT)
            .onErrorReturn("DOWN");

    return Mono.zip(courseCount, enrollmentStatus)
        .map(t -> new CatalogSummary(t.getT1().orElse(null), t.getT2()));
  }

  public record CatalogSummary(Long courseCount, String enrollmentStatus) {}

  record HealthStatus(String status) {}

  record CourseListEnvelope(java.util.List<Object> data) {}
}
