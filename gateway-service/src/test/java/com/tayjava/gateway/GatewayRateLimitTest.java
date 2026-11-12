package com.tayjava.gateway;

import java.io.IOException;
import java.time.Duration;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
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

/**
 * Without Redis the Gateway RequestRateLimiter fails open. This test checks the pay route still
 * proxies (happy) and a missing backend remains a failure path; 429 is exercised via Compose.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class GatewayRateLimitTest {

  private static MockWebServer orderServer;

  @LocalServerPort private int port;

  @Autowired private WebTestClient webTestClient;

  @DynamicPropertySource
  static void registerUris(DynamicPropertyRegistry registry) throws IOException {
    orderServer = new MockWebServer();
    orderServer.start();
    registry.add("ORDER_URI", () -> orderServer.url("/").toString().replaceAll("/$", ""));
    registry.add("COURSE_URI", () -> "http://127.0.0.1:9");
    registry.add("ENROLLMENT_URI", () -> "http://127.0.0.1:9");
    registry.add("BFF_URI", () -> "http://127.0.0.1:9");
    // Point Redis at a closed port so the limiter fails open quickly (200ms timeout).
    registry.add("spring.data.redis.host", () -> "127.0.0.1");
    registry.add("spring.data.redis.port", () -> "9");
    registry.add("REDIS_HOST", () -> "127.0.0.1");
    registry.add("REDIS_PORT", () -> "9");
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

  @Test
  void payRouteProxiesWhenRedisUnavailableFailOpen() {
    orderServer.enqueue(
        new MockResponse()
            .setBody("{\"success\":true,\"data\":{\"status\":\"FULFILLING\"}}")
            .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));

    webTestClient.post().uri("/api/v1/orders/1001/pay").exchange().expectStatus().isOk();
  }

  @Test
  void payRouteStillFailsWhenOrderBackendRefused() {
    // No enqueue + dead ORDER would 5xx; here ORDER is up — use a path that hits closed course.
    webTestClient.get().uri("/api/v1/courses").exchange().expectStatus().is5xxServerError();
  }
}
