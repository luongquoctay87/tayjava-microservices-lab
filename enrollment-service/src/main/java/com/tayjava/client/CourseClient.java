package com.tayjava.client;

/** Outbound Course lookup used by Enrollment before creating a row. */
public interface CourseClient {

  void requirePublished(Long courseId);
}
