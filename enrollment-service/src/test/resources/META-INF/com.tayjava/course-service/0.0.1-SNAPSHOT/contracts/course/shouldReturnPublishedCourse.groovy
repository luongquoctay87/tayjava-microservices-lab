package contracts.course

import org.springframework.cloud.contract.spec.Contract

Contract.make {
  description("GET /api/v1/courses/{id} returns a published course envelope")
  request {
    method GET()
    urlPath("/api/v1/courses/1") {
      // keep path stable for Enrollment RestCourseClient / gRPC-adjacent HTTP clients
    }
    headers {
      header("API-Version", "1")
    }
  }
  response {
    status OK()
    headers {
      contentType(applicationJson())
      header("API-Version", "1")
    }
    body([
      success: true,
      code: "SUCCESS",
      data: [
        id: 1,
        slug: "java-core",
        title: "Java Core from beginner to senior",
        instructorUsername: "bao.teacher",
        published: true
      ]
    ])
  }
}
