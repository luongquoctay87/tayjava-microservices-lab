package com.tayjava.controller;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CourseControllerTest {

  @Autowired private MockMvc mvc;

  @Test
  void listsSeededPublishedCourses() throws Exception {
    mvc.perform(get("/api/v1/courses"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value("SUCCESS"))
        .andExpect(jsonPath("$.data[*].slug", hasItem("java-core")))
        .andExpect(jsonPath("$.data[*].slug", hasItem("spring-security")));
  }

  @Test
  void missingCourseIs404() throws Exception {
    mvc.perform(get("/api/v1/courses/999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value("COURSE_NOT_FOUND"));
  }

  @Test
  void blankTitleIs400() throws Exception {
    mvc.perform(
            post("/api/v1/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"slug":"java-new","title":"  ","instructorUsername":"bao.teacher"}
                    """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
  }

  @Test
  void unsupportedApiVersionIs400() throws Exception {
    mvc.perform(get("/api/v1/courses").header("API-Version", "2"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("UNSUPPORTED_API_VERSION"));
  }
}
