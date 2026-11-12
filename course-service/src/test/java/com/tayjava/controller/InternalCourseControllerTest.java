package com.tayjava.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tayjava.common.security.DemoJwtSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class InternalCourseControllerTest {

  @Autowired private MockMvc mvc;

  @Test
  void missingTokenIs401() throws Exception {
    mvc.perform(get("/internal/courses/1")).andExpect(status().isUnauthorized());
  }

  @Test
  void userTokenWithoutScopeIs403() throws Exception {
    mvc.perform(get("/internal/courses/1").header("Authorization", "Bearer demo:an.student"))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("FORBIDDEN"));
  }

  @Test
  void serviceTokenWithScopeIs200() throws Exception {
    mvc.perform(
            get("/internal/courses/1")
                .header(
                    "Authorization",
                    DemoJwtSupport.serviceBearer(
                        DemoJwtSupport.CLIENT_ENROLLMENT, DemoJwtSupport.SCOPE_COURSE_READ)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
  }
}
