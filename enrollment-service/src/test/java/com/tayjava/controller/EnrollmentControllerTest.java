package com.tayjava.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.tayjava.client.CourseClient;
import com.tayjava.common.enums.ErrorCode;
import com.tayjava.common.exception.ErrorException;
import com.tayjava.service.PaymentStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class EnrollmentControllerTest {

  @Autowired private MockMvc mvc;
  @Autowired private PaymentStub paymentStub;

  @MockitoBean private CourseClient courseClient;

  @Test
  void enrollsPublishedCourse() throws Exception {
    mvc.perform(
            post("/api/v1/enrollments").header("Authorization", "Bearer demo:an.student")
                .header("Idempotency-Key", "enroll-alice-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"studentUsername\":\"an.student\",\"courseId\":1}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.studentUsername").value("an.student"))
        .andExpect(jsonPath("$.data.courseId").value(1))
        .andExpect(jsonPath("$.data.status").value("APPROVED"));

    mvc.perform(get("/api/v1/enrollments").header("Authorization", "Bearer demo:an.student").param("student", "an.student"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[0].studentUsername").value("an.student"));
  }

  @Test
  void sameIdempotencyKeyReturnsSameEnrollmentWithoutSecondCharge() throws Exception {
    String body = "{\"studentUsername\":\"bob.student\",\"courseId\":2}";
    int chargesBefore = paymentStub.chargedKeys();

    MvcResult first =
        mvc.perform(
                post("/api/v1/enrollments").header("Authorization", "Bearer demo:an.student")
                    .header("Idempotency-Key", "enroll-bob-2")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
            .andExpect(status().isCreated())
            .andReturn();
    int enrollmentId = JsonPath.read(first.getResponse().getContentAsString(), "$.data.id");

    mvc.perform(
            post("/api/v1/enrollments").header("Authorization", "Bearer demo:an.student")
                .header("Idempotency-Key", "enroll-bob-2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.id").value(enrollmentId));

    verify(courseClient, times(1)).requirePublished(2L);
    Assertions.assertEquals(chargesBefore + 1, paymentStub.chargedKeys());
  }

  @Test
  void sameKeyDifferentPayloadIs409() throws Exception {
    mvc.perform(
            post("/api/v1/enrollments").header("Authorization", "Bearer demo:an.student")
                .header("Idempotency-Key", "reuse-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"studentUsername\":\"an.student\",\"courseId\":1}"))
        .andExpect(status().isCreated());

    mvc.perform(
            post("/api/v1/enrollments").header("Authorization", "Bearer demo:an.student")
                .header("Idempotency-Key", "reuse-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"studentUsername\":\"an.student\",\"courseId\":2}"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("IDEMPOTENCY_KEY_CONFLICT"));
  }

  @Test
  void missingIdempotencyKeyIs400() throws Exception {
    mvc.perform(
            post("/api/v1/enrollments").header("Authorization", "Bearer demo:an.student")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"studentUsername\":\"an.student\",\"courseId\":1}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("IDEMPOTENCY_KEY_REQUIRED"));
  }

  @Test
  void blankUsernameIs400() throws Exception {
    mvc.perform(
            post("/api/v1/enrollments").header("Authorization", "Bearer demo:an.student")
                .header("Idempotency-Key", "blank-user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"studentUsername\":\"  \",\"courseId\":1}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
  }

  @Test
  void nonPositiveCourseIdIs400() throws Exception {
    mvc.perform(
            post("/api/v1/enrollments").header("Authorization", "Bearer demo:an.student")
                .header("Idempotency-Key", "bad-course")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"studentUsername\":\"an.student\",\"courseId\":0}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
  }

  @Test
  void unpublishedCourseIs422() throws Exception {
    doThrow(new ErrorException(ErrorCode.COURSE_NOT_ENROLLABLE, "Course is not published"))
        .when(courseClient)
        .requirePublished(3L);

    mvc.perform(
            post("/api/v1/enrollments").header("Authorization", "Bearer demo:an.student")
                .header("Idempotency-Key", "draft-course")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"studentUsername\":\"an.student\",\"courseId\":3}"))
        .andExpect(status().isUnprocessableContent())
        .andExpect(jsonPath("$.code").value("COURSE_NOT_ENROLLABLE"));
  }

  @Test
  void courseDownIs503() throws Exception {
    doThrow(new ErrorException(ErrorCode.COURSE_UNAVAILABLE, "Course service unavailable"))
        .when(courseClient)
        .requirePublished(1L);

    mvc.perform(
            post("/api/v1/enrollments").header("Authorization", "Bearer demo:an.student")
                .header("Idempotency-Key", "course-down")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"studentUsername\":\"an.student\",\"courseId\":1}"))
        .andExpect(status().isServiceUnavailable())
        .andExpect(jsonPath("$.code").value("COURSE_UNAVAILABLE"));
  }
}
