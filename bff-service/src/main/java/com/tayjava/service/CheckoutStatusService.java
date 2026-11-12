package com.tayjava.service;

import com.tayjava.common.dto.bff.CheckoutStatusResp;

public interface CheckoutStatusService {

  CheckoutStatusResp getCheckoutStatus(Long orderId);
}
