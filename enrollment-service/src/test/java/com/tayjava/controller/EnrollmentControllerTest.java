package com.tayjava.controller;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tayjava.client.CourseClient;
import com.tayjava.common.enums.ErrorCode;
import com.tayjava.common.exception.ErrorException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class EnrollmentControllerTest {

  @Autowired private MockMvc mvc;

  @MockitoBean private CourseClient courseClient;

  @Test
  void enrollsPublishedCourse() throws Exception {
    mvc.perform(
            post("/api/enrollments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"studentUsername\":\"an.student\",\"courseId\":1}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.studentUsername").value("an.student"))
        .andExpect(jsonPath("$.data.courseId").value(1))
        .andExpect(jsonPath("$.data.status").value("APPROVED"));

    mvc.perform(get("/api/enrollments").param("student", "an.student"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[0].studentUsername").value("an.student"));
  }

  @Test
  void blankUsernameIs400() throws Exception {
    mvc.perform(
            post("/api/enrollments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"studentUsername\":\"  \",\"courseId\":1}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
  }

  @Test
  void nonPositiveCourseIdIs400() throws Exception {
    mvc.perform(
            post("/api/enrollments")
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
            post("/api/enrollments")
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
            post("/api/enrollments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"studentUsername\":\"an.student\",\"courseId\":1}"))
        .andExpect(status().isServiceUnavailable())
        .andExpect(jsonPath("$.code").value("COURSE_UNAVAILABLE"));
  }
}
