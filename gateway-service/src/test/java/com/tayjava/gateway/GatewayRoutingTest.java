package com.tayjava.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class GatewayRoutingTest {

  private static MockWebServer courseServer;
  private static MockWebServer enrollmentServer;

  @LocalServerPort private int port;

  @Autowired private WebTestClient webTestClient;

  @DynamicPropertySource
  static void registerUris(DynamicPropertyRegistry registry) throws IOException {
    courseServer = new MockWebServer();
    enrollmentServer = new MockWebServer();
    courseServer.start();
    enrollmentServer.start();
    registry.add("COURSE_URI", () -> courseServer.url("/").toString().replaceAll("/$", ""));
    registry.add(
        "ENROLLMENT_URI", () -> enrollmentServer.url("/").toString().replaceAll("/$", ""));
    registry.add("ORDER_URI", () -> "http://127.0.0.1:9");
    registry.add("BFF_URI", () -> "http://127.0.0.1:9");
  }

  @BeforeEach
  void configureClient() {
    webTestClient =
        webTestClient
            .mutate()
            .responseTimeout(Duration.ofSeconds(5))
            .baseUrl("http://localhost:" + port)
            .build();
  }

  @AfterEach
  void drainPending() throws InterruptedException {
    while (courseServer.takeRequest(1, TimeUnit.MILLISECONDS) != null) {
      // drain
    }
    while (enrollmentServer.takeRequest(1, TimeUnit.MILLISECONDS) != null) {
      // drain
    }
  }

  @Test
  void routesCoursesAndPropagatesCorrelationId() throws Exception {
    courseServer.enqueue(
        new MockResponse()
            .setBody("{\"success\":true,\"data\":[]}")
            .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));

    webTestClient
        .get()
        .uri("/api/v1/courses")
        .header(CorrelationIdFilter.HEADER, "corr-demo-123456")
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .valueEquals(CorrelationIdFilter.HEADER, "corr-demo-123456")
        .expectBody()
        .jsonPath("$.success")
        .isEqualTo(true);

    RecordedRequest recorded = courseServer.takeRequest(2, TimeUnit.SECONDS);
    assertThat(recorded).isNotNull();
    assertThat(recorded.getPath()).isEqualTo("/api/v1/courses");
    assertThat(recorded.getHeader(CorrelationIdFilter.HEADER)).isEqualTo("corr-demo-123456");
  }

  @Test
  void replacesUnsafeCorrelationId() {
    courseServer.enqueue(
        new MockResponse()
            .setBody("{\"success\":true}")
            .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));

    webTestClient
        .get()
        .uri("/api/v1/courses")
        .header(CorrelationIdFilter.HEADER, "!!!")
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .value(
            CorrelationIdFilter.HEADER, value -> assertThat(value).matches("[A-Za-z0-9-]{8,64}"));
  }

  @Test
  void enrollmentRouteStaysUpWhenOtherBackendIsRefused() {
    enrollmentServer.enqueue(
        new MockResponse()
            .setBody("{\"success\":true,\"data\":[]}")
            .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));

    webTestClient
        .get()
        .uri("/api/v1/enrollments?student=an.student")
        .exchange()
        .expectStatus()
        .isOk();

    // ORDER_URI points at a closed port (connection refused → 5xx with Gateway 5 defaults).
    webTestClient.get().uri("/api/v1/orders?student=an.student").exchange().expectStatus().is5xxServerError();
  }

  @Test
  void unknownPathIsNotFound() {
    webTestClient.get().uri("/api/unknown").exchange().expectStatus().isNotFound();
  }
}
