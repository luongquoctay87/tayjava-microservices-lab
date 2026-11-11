package com.tayjava.client;

/** Raw outbound Course call (gRPC / RestClient / WebClient) before resilience decorators. */
public interface CourseTransport {

  void requirePublished(Long courseId);
}
