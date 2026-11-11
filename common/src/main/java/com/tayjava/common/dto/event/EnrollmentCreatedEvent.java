package com.tayjava.common.dto.event;

import java.time.Instant;

/**
 * Event published when an enrollment is created. Payload is JSON over Kafka topic {@code
 * enrollment.events}.
 */
public record EnrollmentCreatedEvent(
    String eventId,
    Long enrollmentId,
    String studentUsername,
    Long courseId,
    String status,
    Instant occurredAt) {}
