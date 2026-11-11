package com.tayjava.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(
    properties = {
      "tayjava.clients.course-transport=web",
      "spring.grpc.client.enabled=false"
    })
class CourseClientSwitchTest {

  @Autowired private CourseTransport courseTransport;
  @Autowired private CourseClient courseClient;

  @Test
  void selectsWebTransportAndResilientFacade() {
    Assertions.assertInstanceOf(WebCourseClient.class, courseTransport);
    Assertions.assertInstanceOf(ResilientCourseClient.class, courseClient);
  }
}
