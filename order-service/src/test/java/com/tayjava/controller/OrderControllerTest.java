package com.tayjava.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tayjava.common.dto.event.EnrollmentCreatedEvent;
import com.tayjava.service.OrderService;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

  @Autowired private MockMvc mvc;
  @Autowired private OrderService orderService;

  @Test
  void listsOrdersForStudent() throws Exception {
    orderService.createFromEnrollmentEvent(
        new EnrollmentCreatedEvent(
            "evt-bob", 55L, "bob@example.com", 3L, "APPROVED", Instant.now()));

    mvc.perform(get("/api/v1/orders").param("student", "bob@example.com"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data[0].enrollmentId").value(55))
        .andExpect(jsonPath("$.data[0].studentUsername").value("bob@example.com"));
  }
}
