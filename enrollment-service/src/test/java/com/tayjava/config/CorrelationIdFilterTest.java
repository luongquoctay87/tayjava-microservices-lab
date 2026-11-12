package com.tayjava.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tayjava.common.observability.CorrelationIds;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class CorrelationIdFilterTest {

  @Autowired private MockMvc mvc;

  @Test
  void keepsValidIncomingCorrelationId() throws Exception {
    mvc.perform(
            get("/api/v1/enrollments")
                .param("student", "an.student")
                .header("Authorization", "Bearer demo:an.student")
                .header(CorrelationIds.HEADER, "checkout-lab-01"))
        .andExpect(status().isOk())
        .andExpect(header().string(CorrelationIds.HEADER, "checkout-lab-01"));
  }

  @Test
  void generatesWhenMissing() throws Exception {
    MvcResult result =
        mvc.perform(
                get("/api/v1/enrollments")
                    .param("student", "an.student")
                    .header("Authorization", "Bearer demo:an.student"))
            .andExpect(status().isOk())
            .andExpect(header().exists(CorrelationIds.HEADER))
            .andReturn();
    assertThat(CorrelationIds.isValid(result.getResponse().getHeader(CorrelationIds.HEADER)))
        .isTrue();
  }
}
