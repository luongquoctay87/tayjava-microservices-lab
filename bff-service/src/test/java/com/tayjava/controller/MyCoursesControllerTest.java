package com.tayjava.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tayjava.common.dto.bff.MyCourseResp;
import com.tayjava.common.enums.EEnrollmentStatus;
import com.tayjava.common.enums.ErrorCode;
import com.tayjava.common.exception.ErrorException;
import com.tayjava.service.MyCoursesService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class MyCoursesControllerTest {

  @Autowired private MockMvc mvc;

  @MockitoBean private MyCoursesService myCoursesService;

  @Test
  void returnsComposedCourses() throws Exception {
    when(myCoursesService.listMyCourses("an.student"))
        .thenReturn(
            List.of(
                MyCourseResp.builder()
                    .enrollmentId(10L)
                    .courseId(1L)
                    .slug("java-core")
                    .title("Java Core")
                    .instructorUsername("bao.teacher")
                    .status(EEnrollmentStatus.APPROVED)
                    .build()));

    mvc.perform(get("/api/v1/me/courses").param("student", "an.student").header("API-Version", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data[0].slug").value("java-core"))
        .andExpect(jsonPath("$.data[0].status").value("APPROVED"));
  }

  @Test
  void upstreamFailureIsPropagated() throws Exception {
    when(myCoursesService.listMyCourses("an.student"))
        .thenThrow(new ErrorException(ErrorCode.COURSE_UNAVAILABLE, "Course service unavailable"));

    mvc.perform(get("/api/v1/me/courses").param("student", "an.student"))
        .andExpect(status().isServiceUnavailable())
        .andExpect(jsonPath("$.code").value("COURSE_UNAVAILABLE"));
  }
}
