package com.tayjava.config;

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
class ZeroTrustAuthFilterTest {

  @Autowired private MockMvc mvc;

  @Test
  void forgedUserHeaderIsNotIdentity() throws Exception {
    mvc.perform(
            post("/api/v1/courses")
                .header("X-User-Id", "admin")
                .header("X-Roles", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"slug":"x","title":"X","instructorUsername":"bao.teacher"}
                    """))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void missingBearerIsUnauthorizedOnWrite() throws Exception {
    mvc.perform(
            post("/api/v1/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"slug":"x","title":"X","instructorUsername":"bao.teacher"}
                    """))
        .andExpect(status().isUnauthorized());
  }
}
