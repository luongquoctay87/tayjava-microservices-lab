package com.tayjava.demo.strangler;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class StranglerWeightRoutingTest {

  private static MockWebServer courseServer;
  private static MockWebServer monolithServer;

  @LocalServerPort private int port;

  @Autowired private WebTestClient webTestClient;

  @DynamicPropertySource
  static void registerUris(DynamicPropertyRegistry registry) throws IOException {
    courseServer = new MockWebServer();
    monolithServer = new MockWebServer();
    courseServer.start();
    monolithServer.start();
    registry.add("COURSE_URI", () -> courseServer.url("/").toString().replaceAll("/$", ""));
    registry.add("MONOLITH_URI", () -> monolithServer.url("/").toString().replaceAll("/$", ""));
  }

  @AfterAll
  static void shutdown() throws IOException {
    courseServer.shutdown();
    monolithServer.shutdown();
  }

  @BeforeEach
  void configureClient() {
    webTestClient =
        webTestClient
            .mutate()
            .responseTimeout(Duration.ofSeconds(5))
            .baseUrl("http://localhost:" + port)
            .build();

    courseServer.setDispatcher(
        new Dispatcher() {
          @Override
          public MockResponse dispatch(RecordedRequest request) {
            return json("{\"success\":true,\"data\":[{\"servedBy\":\"course-service\"}]}");
          }
        });
    monolithServer.setDispatcher(
        new Dispatcher() {
          @Override
          public MockResponse dispatch(RecordedRequest request) {
            if ("POST".equals(request.getMethod())) {
              return json("{\"success\":true,\"data\":\"created-on-monolith\"}");
            }
            return json("{\"success\":true,\"data\":[{\"servedBy\":\"monolith\"}]}");
          }
        });
  }

  private static MockResponse json(String body) {
    return new MockResponse()
        .setBody(body)
        .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
  }

  @Test
  void weightedCourseReadsHitBothBackends() {
    Set<String> bodies = new HashSet<>();
    for (int i = 0; i < 40; i++) {
      String body =
          webTestClient
              .get()
              .uri("/api/courses")
              .exchange()
              .expectStatus()
              .isOk()
              .expectBody(String.class)
              .returnResult()
              .getResponseBody();
      bodies.add(body);
    }
    assertThat(bodies.stream().anyMatch(b -> b != null && b.contains("course-service"))).isTrue();
    assertThat(bodies.stream().anyMatch(b -> b != null && b.contains("monolith"))).isTrue();
  }

  @Test
  void nonGetFallsThroughToMonolithCatchAll() throws InterruptedException {
    // Clear prior recorded traffic from the weighted test when suites share servers.
    while (courseServer.takeRequest(1, TimeUnit.MILLISECONDS) != null) {}
    while (monolithServer.takeRequest(1, TimeUnit.MILLISECONDS) != null) {}

    webTestClient
        .post()
        .uri("/api/courses")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue("{}")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .value(body -> assertThat(body).contains("created-on-monolith"));

    RecordedRequest recorded = monolithServer.takeRequest(2, TimeUnit.SECONDS);
    assertThat(recorded).isNotNull();
    assertThat(recorded.getMethod()).isEqualTo("POST");
  }
}
