package com.tayjava.config;

import com.tayjava.common.observability.CorrelationIds;
import com.tayjava.common.security.DemoJwtSupport;
import java.net.http.HttpClient;
import java.time.Duration;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.client.reactive.JdkClientHttpConnector;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class CourseClientConfig {

  /** Lab-only client-credentials stand-in (no Authorization Server). */
  private static final String SERVICE_BEARER =
      DemoJwtSupport.serviceBearer(
          DemoJwtSupport.CLIENT_ENROLLMENT, DemoJwtSupport.SCOPE_COURSE_READ);

  @Bean
  RestClient courseRestClient(
      RestClient.Builder builder,
      @Value("${tayjava.clients.course-base-url}") String baseUrl,
      @Value("${tayjava.clients.course-connect-timeout}") Duration connectTimeout,
      @Value("${tayjava.clients.course-read-timeout}") Duration readTimeout) {
    HttpClient httpClient = HttpClient.newBuilder().connectTimeout(connectTimeout).build();
    JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
    factory.setReadTimeout(readTimeout);
    return builder
        .baseUrl(baseUrl)
        .defaultHeader(DemoJwtSupport.HEADER, SERVICE_BEARER)
        .requestFactory(factory)
        .requestInterceptor(
            (request, body, execution) -> {
              String cid = MDC.get(CorrelationIds.MDC_KEY);
              if (CorrelationIds.isValid(cid)) {
                request.getHeaders().set(CorrelationIds.HEADER, cid);
              }
              return execution.execute(request, body);
            })
        .build();
  }

  @Bean
  WebClient courseWebClient(
      WebClient.Builder builder,
      @Value("${tayjava.clients.course-base-url}") String baseUrl,
      @Value("${tayjava.clients.course-connect-timeout}") Duration connectTimeout,
      @Value("${tayjava.clients.course-read-timeout}") Duration readTimeout) {
    HttpClient httpClient = HttpClient.newBuilder().connectTimeout(connectTimeout).build();
    return builder
        .baseUrl(baseUrl)
        .defaultHeader(DemoJwtSupport.HEADER, SERVICE_BEARER)
        .clientConnector(new JdkClientHttpConnector(httpClient))
        .filter(
            (request, next) -> {
              String cid = MDC.get(CorrelationIds.MDC_KEY);
              if (CorrelationIds.isValid(cid)) {
                return next.exchange(
                    ClientRequest.from(request).header(CorrelationIds.HEADER, cid).build());
              }
              return next.exchange(request);
            })
        .build();
  }
}
