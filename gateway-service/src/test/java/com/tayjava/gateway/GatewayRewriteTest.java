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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class GatewayRewriteTest {

  private static MockWebServer courseServer;
  private static MockWebServer enrollmentServer;
  private static MockWebServer bffServer;

  @LocalServerPort private int port;

  @Autowired private WebTestClient webTestClient;

  @DynamicPropertySource
  static void registerUris(DynamicPropertyRegistry registry) throws IOException {
    courseServer = new MockWebServer();
    enrollmentServer = new MockWebServer();
    bffServer = new MockWebServer();
    courseServer.start();
    enrollmentServer.start();
    bffServer.start();
    registry.add("COURSE_URI", () -> courseServer.url("/").toString().replaceAll("/$", ""));
    registry.add(
        "ENROLLMENT_URI", () -> enrollmentServer.url("/").toString().replaceAll("/$", ""));
    registry.add("BFF_URI", () -> bffServer.url("/").toString().replaceAll("/$", ""));
    registry.add("ORDER_URI", () -> "http://127.0.0.1:9");
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
  void drain() throws InterruptedException {
    while (courseServer.takeRequest(1, TimeUnit.MILLISECONDS) != null) {}
    while (enrollmentServer.takeRequest(1, TimeUnit.MILLISECONDS) != null) {}
    while (bffServer.takeRequest(1, TimeUnit.MILLISECONDS) != null) {}
  }

  @Test
  void rewritePublicApiCoursesToV1() throws Exception {
    courseServer.enqueue(
        new MockResponse()
            .setBody("{\"success\":true,\"data\":[]}")
            .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));

    webTestClient.get().uri("/api/courses/7").exchange().expectStatus().isOk();

    RecordedRequest recorded = courseServer.takeRequest(2, TimeUnit.SECONDS);
    assertThat(recorded).isNotNull();
    assertThat(recorded.getPath()).isEqualTo("/api/v1/courses/7");
  }

  @Test
  void stripPrefixLegacyCoursePath() throws Exception {
    courseServer.enqueue(
        new MockResponse()
            .setBody("{\"success\":true}")
            .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));

    webTestClient.get().uri("/course-service/api/v1/courses/3").exchange().expectStatus().isOk();

    RecordedRequest recorded = courseServer.takeRequest(2, TimeUnit.SECONDS);
    assertThat(recorded).isNotNull();
    assertThat(recorded.getPath()).isEqualTo("/api/v1/courses/3");
  }

  @Test
  void featuredUsesSetPathToBffBeforeCourseCatchAll() throws Exception {
    bffServer.enqueue(
        new MockResponse()
            .setBody("{\"success\":true,\"data\":[]}")
            .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));

    webTestClient.get().uri("/api/v1/courses/featured").exchange().expectStatus().isOk();

    RecordedRequest recorded = bffServer.takeRequest(2, TimeUnit.SECONDS);
    assertThat(recorded).isNotNull();
    assertThat(recorded.getPath()).isEqualTo("/api/v1/featured-courses");
    // Course catch-all must not win: no new course request for this exchange.
    assertThat(courseServer.takeRequest(50, TimeUnit.MILLISECONDS)).isNull();
  }

  @Test
  void catalogSummarySubstitutesWhenEnrollmentDown() {
    courseServer.enqueue(
        new MockResponse()
            .setBody("{\"success\":true,\"data\":[{},{}]}")
            .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));

    webTestClient
        .get()
        .uri("/api/catalog/summary")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.courseCount")
        .isEqualTo(2)
        .jsonPath("$.enrollmentStatus")
        .isEqualTo("DOWN");
  }
}
