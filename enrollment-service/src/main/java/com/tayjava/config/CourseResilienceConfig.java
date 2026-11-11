package com.tayjava.config;

import com.tayjava.common.enums.ErrorCode;
import com.tayjava.common.exception.ErrorException;
import io.github.resilience4j.common.circuitbreaker.configuration.CircuitBreakerConfigCustomizer;
import io.github.resilience4j.common.retry.configuration.RetryConfigCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CourseResilienceConfig {

  @Bean
  CircuitBreakerConfigCustomizer courseCircuitBreakerCustomizer() {
    return CircuitBreakerConfigCustomizer.of(
        "course",
        builder ->
            builder.recordException(
                ex ->
                    ex instanceof ErrorException error
                        && error.getErrorCode() == ErrorCode.COURSE_UNAVAILABLE));
  }

  @Bean
  RetryConfigCustomizer courseRetryCustomizer() {
    return RetryConfigCustomizer.of(
        "course",
        builder ->
            builder.retryOnException(
                ex ->
                    ex instanceof ErrorException error
                        && error.getErrorCode() == ErrorCode.COURSE_UNAVAILABLE));
  }
}
