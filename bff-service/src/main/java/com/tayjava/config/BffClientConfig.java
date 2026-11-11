package com.tayjava.config;

import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class BffClientConfig {

  @Bean
  RestClient courseRestClient(
      RestClient.Builder builder,
      @Value("${tayjava.clients.course-base-url}") String baseUrl,
      @Value("${tayjava.clients.connect-timeout}") Duration connectTimeout,
      @Value("${tayjava.clients.read-timeout}") Duration readTimeout) {
    return builder.baseUrl(baseUrl).requestFactory(factory(connectTimeout, readTimeout)).build();
  }

  @Bean
  RestClient enrollmentRestClient(
      RestClient.Builder builder,
      @Value("${tayjava.clients.enrollment-base-url}") String baseUrl,
      @Value("${tayjava.clients.connect-timeout}") Duration connectTimeout,
      @Value("${tayjava.clients.read-timeout}") Duration readTimeout) {
    return builder.baseUrl(baseUrl).requestFactory(factory(connectTimeout, readTimeout)).build();
  }

  private static JdkClientHttpRequestFactory factory(Duration connectTimeout, Duration readTimeout) {
    HttpClient httpClient = HttpClient.newBuilder().connectTimeout(connectTimeout).build();
    JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
    requestFactory.setReadTimeout(readTimeout);
    return requestFactory;
  }
}
