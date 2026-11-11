package com.tayjava.contract;

import com.tayjava.client.RestCourseClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.StubFinder;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

/**
 * Consumer-side contract test: Enrollment RestCourseClient against Course stubs generated from
 * Spring Cloud Contract mappings (classpath stubs, no live Course process).
 */
@SpringBootTest
@AutoConfigureStubRunner(
    ids = "com.tayjava:course-service:0.0.1-SNAPSHOT:stubs",
    stubsMode = StubRunnerProperties.StubsMode.CLASSPATH)
@TestPropertySource(
    properties = {
      "tayjava.clients.course-transport=rest",
      "spring.grpc.client.enabled=false"
    })
class CourseClientContractTest {

  @Autowired private StubFinder stubFinder;

  @Test
  void restCourseClientAcceptsContractStubForPublishedCourse() {
    int port = stubFinder.findStubUrl("course-service").getPort();
    RestClient restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();
    new RestCourseClient(restClient).requirePublished(1L);
  }
}
