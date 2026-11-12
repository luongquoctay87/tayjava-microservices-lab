package com.tayjava.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

class RateLimitKeyConfigTest {

  @Test
  void ipKeyIgnoresClientSuppliedXForwardedForWhenTrustedProxyCountIsZero() {
    RateLimitKeyConfig config = new RateLimitKeyConfig(0);
    KeyResolver resolver = config.ipKeyResolver();

    MockServerHttpRequest request =
        MockServerHttpRequest.get("/api/v1/courses")
            .remoteAddress(new InetSocketAddress("10.0.0.8", 12345))
            .header("X-Forwarded-For", "9.9.9.9")
            .build();

    String key = resolver.resolve(MockServerWebExchange.from(request)).block();
    assertThat(key).isEqualTo("ip:10.0.0.8");
  }

  @Test
  void userOrIpFallsBackToIpWithoutPrincipal() {
    RateLimitKeyConfig config = new RateLimitKeyConfig(0);
    KeyResolver resolver = config.userOrIpKeyResolver();

    MockServerHttpRequest request =
        MockServerHttpRequest.post("/api/v1/orders/1/pay")
            .remoteAddress(new InetSocketAddress("10.0.0.9", 12345))
            .build();

    String key = resolver.resolve(MockServerWebExchange.from(request)).block();
    assertThat(key).isEqualTo("ip:10.0.0.9");
  }
}
