package com.tayjava.gateway;

import java.net.InetSocketAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.support.ipresolver.XForwardedRemoteAddressResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimitKeyConfig {

  private final int trustedProxyCount;

  public RateLimitKeyConfig(
      @Value("${tayjava.gateway.trusted-proxy-count:0}") int trustedProxyCount) {
    // 0 = Gateway accepts the client directly and ignores a client-supplied X-Forwarded-For.
    this.trustedProxyCount = trustedProxyCount;
  }

  @Bean
  @Primary
  KeyResolver ipKeyResolver() {
    return exchange -> clientIp(exchange).map(ip -> "ip:" + ip);
  }

  @Bean
  KeyResolver userOrIpKeyResolver() {
    return exchange ->
        exchange
            .getPrincipal()
            .map(principal -> "user:" + principal.getName())
            .switchIfEmpty(Mono.defer(() -> clientIp(exchange).map(ip -> "ip:" + ip)));
  }

  private Mono<String> clientIp(ServerWebExchange exchange) {
    InetSocketAddress address =
        trustedProxyCount > 0
            ? XForwardedRemoteAddressResolver.maxTrustedIndex(trustedProxyCount).resolve(exchange)
            : exchange.getRequest().getRemoteAddress();
    return address == null ? Mono.empty() : Mono.just(address.getHostString());
  }
}
