package com.tayjava.gateway;

import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Accepts a safe client correlation id or generates a UUID. Downstream services may log the same
 * header; it is not a substitute for an OpenTelemetry trace id.
 */
@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

  static final String HEADER = "X-Correlation-Id";
  private static final Pattern SAFE_ID = Pattern.compile("[A-Za-z0-9-]{8,64}");

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    String incoming = exchange.getRequest().getHeaders().getFirst(HEADER);
    String correlationId =
        incoming != null && SAFE_ID.matcher(incoming).matches()
            ? incoming
            : UUID.randomUUID().toString();

    ServerHttpRequest request = exchange.getRequest().mutate().header(HEADER, correlationId).build();
    exchange.getResponse().getHeaders().set(HEADER, correlationId);
    return chain.filter(exchange.mutate().request(request).build());
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }
}
