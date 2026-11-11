package com.tayjava.config;

import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.client.reactive.JdkClientHttpConnector;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class CourseClientConfig {

  @Bean
  RestClient courseRestClient(
      RestClient.Builder builder,
      @Value("${tayjava.clients.course-base-url}") String baseUrl,
      @Value("${tayjava.clients.course-connect-timeout}") Duration connectTimeout,
      @Value("${tayjava.clients.course-read-timeout}") Duration readTimeout) {
    HttpClient httpClient = HttpClient.newBuilder().connectTimeout(connectTimeout).build();
    JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
    factory.setReadTimeout(readTimeout);
    return builder.baseUrl(baseUrl).requestFactory(factory).build();
  }

  @Bean
  WebClient courseWebClient(
      WebClient.Builder builder,
      @Value("${tayjava.clients.course-base-url}") String baseUrl,
      @Value("${tayjava.clients.course-connect-timeout}") Duration connectTimeout,
      @Value("${tayjava.clients.course-read-timeout}") Duration readTimeout) {
    HttpClient httpClient = HttpClient.newBuilder().connectTimeout(connectTimeout).build();
    return builder.baseUrl(baseUrl).clientConnector(new JdkClientHttpConnector(httpClient)).build();
  }
}
