package com.tayjava.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tayjava.common.dto.event.EnrollmentCreatedEvent;
import com.tayjava.common.dto.order.CreateOrderReq;
import com.tayjava.common.dto.order.OrderResp;
import com.tayjava.service.OrderService;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
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
            "evt-bob", 55L, "bob@example.com", 3L, "APPROVED", Instant.now(), null, null));

    mvc.perform(
            get("/api/v1/orders")
                .header("Authorization", "Bearer demo:an.student")
                .param("student", "bob@example.com"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data[0].enrollmentId").value(55))
        .andExpect(jsonPath("$.data[0].studentUsername").value("bob@example.com"));
  }

  @Test
  void checkoutPayAndPollUntilFulfilled() throws Exception {
    CreateOrderReq req = new CreateOrderReq();
    req.setStudentUsername("dave@example.com");
    req.setCourseId(11L);
    OrderResp created = orderService.createCheckout(req);
    long orderId = created.getId();

    mvc.perform(
            post("/api/v1/orders")
                .header("Authorization", "Bearer demo:an.student")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"studentUsername":"erin@example.com","courseId":12}
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.status").value("CREATED"))
        .andExpect(jsonPath("$.data.enrollmentId").doesNotExist());

    mvc.perform(
            post("/api/v1/orders/{id}/pay", orderId)
                .header("Authorization", "Bearer demo:an.student"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.status").value("FULFILLING"))
        .andExpect(jsonPath("$.data.enrollmentId").doesNotExist());

    mvc.perform(
            get("/api/v1/orders/{id}", orderId)
                .header("Authorization", "Bearer demo:an.student"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.status").value("FULFILLING"));

    orderService.createFromEnrollmentEvent(
        new EnrollmentCreatedEvent(
            "evt-dave-enroll",
            77L,
            "dave@example.com",
            11L,
            "APPROVED",
            Instant.now(),
            null,
            null));

    mvc.perform(
            get("/api/v1/orders/{id}", orderId)
                .header("Authorization", "Bearer demo:an.student"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.status").value("FULFILLED"))
        .andExpect(jsonPath("$.data.enrollmentId").value(77));
  }
}
