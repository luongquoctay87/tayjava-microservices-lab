package com.tayjava.messaging;

import com.tayjava.common.dto.event.EnrollmentCreatedEvent;
import com.tayjava.common.dto.training.enrollment.EnrollmentResp;

public interface EnrollmentEventPublisher {

  void publishEnrollmentCreated(EnrollmentResp enrollment);

  EnrollmentCreatedEvent lastPublished();
}
