package com.tayjava.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.tayjava.client.OrderApiClient;
import com.tayjava.common.dto.bff.CheckoutStatusResp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CheckoutStatusServiceImplTest {

  @Mock private OrderApiClient orderApiClient;
  @InjectMocks private CheckoutStatusServiceImpl checkoutStatusService;

  @Test
  void mapsFulfillingOrderWithoutInventingEnrollment() {
    OrderApiClient.OrderItem order = new OrderApiClient.OrderItem();
    order.setId(55L);
    order.setCourseId(9L);
    order.setStatus("FULFILLING");

    when(orderApiClient.getById(55L)).thenReturn(order);

    CheckoutStatusResp resp = checkoutStatusService.getCheckoutStatus(55L);

    assertThat(resp.getOrderId()).isEqualTo(55L);
    assertThat(resp.getEnrollmentId()).isNull();
    assertThat(resp.getStatus()).isEqualTo("FULFILLING");
    assertThat(resp.getMessage()).isEqualTo("Order #55 is being processed");
  }
}
