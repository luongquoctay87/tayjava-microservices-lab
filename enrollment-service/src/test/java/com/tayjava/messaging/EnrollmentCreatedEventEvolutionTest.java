package com.tayjava.messaging;

import static org.assertj.core.api.Assertions.assertThat;

import com.tayjava.common.dto.event.EnrollmentCreatedEvent;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

class EnrollmentCreatedEventEvolutionTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void v1PayloadStillDeserializesWithDefaultSchemaVersion() throws Exception {
    String v1 =
        """
        {"eventId":"e1","enrollmentId":10,"studentUsername":"an.student","courseId":1,"status":"APPROVED","occurredAt":"2026-01-01T00:00:00Z"}
        """;
    EnrollmentCreatedEvent event = mapper.readValue(v1, EnrollmentCreatedEvent.class);
    assertThat(event.schemaVersion()).isEqualTo(1);
    assertThat(event.studentEmail()).isNull();
    assertThat(event.enrollmentId()).isEqualTo(10L);
  }

  @Test
  void v2PayloadCarriesOptionalEmail() throws Exception {
    EnrollmentCreatedEvent event =
        EnrollmentCreatedEvent.v2(
            "e2", 11L, "an.student", 2L, "APPROVED", Instant.parse("2026-01-02T00:00:00Z"), "alice@example.com");
    String json = mapper.writeValueAsString(event);
    EnrollmentCreatedEvent roundTrip = mapper.readValue(json, EnrollmentCreatedEvent.class);
    assertThat(roundTrip.schemaVersion()).isEqualTo(2);
    assertThat(roundTrip.studentEmail()).isEqualTo("alice@example.com");
  }
}
