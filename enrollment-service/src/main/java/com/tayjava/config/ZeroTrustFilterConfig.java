package com.tayjava.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class ZeroTrustFilterConfig {

  @Bean
  FilterRegistrationBean<ZeroTrustAuthFilter> zeroTrustAuthFilterRegistration(
      ZeroTrustAuthFilter filter) {
    FilterRegistrationBean<ZeroTrustAuthFilter> registration = new FilterRegistrationBean<>();
    registration.setFilter(filter);
    registration.addUrlPatterns("/*");
    registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 20);
    registration.setName("zeroTrustAuthFilter");
    return registration;
  }
}
