package com.tayjava.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tayjava.common.dto.bff.CheckoutStatusResp;
import com.tayjava.service.CheckoutStatusService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CheckoutStatusControllerTest {

  @Autowired private MockMvc mvc;

  @MockitoBean private CheckoutStatusService checkoutStatusService;

  @Test
  void returnsFulfillingCheckoutStatus() throws Exception {
    when(checkoutStatusService.getCheckoutStatus(1001L))
        .thenReturn(
            CheckoutStatusResp.builder()
                .orderId(1001L)
                .courseId(7L)
                .status("FULFILLING")
                .message("Order #1001 is being processed")
                .build());

    mvc.perform(
            get("/api/v1/checkout-status/1001")
                .header("Authorization", "Bearer demo:an.student"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.status").value("FULFILLING"))
        .andExpect(jsonPath("$.data.message").value("Order #1001 is being processed"));
  }
}
