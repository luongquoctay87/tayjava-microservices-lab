package com.tayjava.demo.strangler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Interception point for Strangler Fig: weighted GET /api/courses between monolith and extracted
 * course-service. Not part of the continuous project line.
 */
@SpringBootApplication
public class StranglerGatewayApplication {

  public static void main(String[] args) {
    SpringApplication.run(StranglerGatewayApplication.class, args);
  }
}
