package com.tayjava.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tayjava.client.CourseClient;
import com.tayjava.common.dto.training.enrollment.EnrollmentReq;
import com.tayjava.common.dto.training.enrollment.EnrollmentResp;
import com.tayjava.common.enums.EEnrollmentStatus;
import com.tayjava.repository.EnrollmentRepository;
import io.micrometer.observation.tck.TestObservationRegistry;
import io.micrometer.observation.tck.TestObservationRegistryAssert;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EnrollmentObservationTest {

  @Mock private EnrollmentRepository enrollmentRepository;
  @Mock private EnrollmentWriter enrollmentWriter;
  @Mock private CourseClient courseClient;
  @Mock private PaymentStub paymentStub;

  private TestObservationRegistry observationRegistry;
  private EnrollmentServiceImpl service;

  @BeforeEach
  void setUp() {
    observationRegistry = TestObservationRegistry.create();
    service =
        new EnrollmentServiceImpl(
            enrollmentRepository, enrollmentWriter, courseClient, paymentStub, observationRegistry);
  }

  @Test
  void enrollCreatesObservation() {
    EnrollmentReq req = new EnrollmentReq();
    req.setStudentUsername("an.student");
    req.setCourseId(1L);
    EnrollmentResp resp =
        EnrollmentResp.builder()
            .id(10L)
            .studentUsername("an.student")
            .courseId(1L)
            .status(EEnrollmentStatus.APPROVED)
            .build();
    when(enrollmentWriter.findReplay(anyString(), anyString(), anyString()))
        .thenReturn(Optional.empty());
    when(enrollmentWriter.persistNew(eq(req), anyString(), anyString(), anyString()))
        .thenReturn(resp);

    EnrollmentResp result = service.enroll(req, "key-1");

    assertThat(result.getId()).isEqualTo(10L);
    verify(courseClient).requirePublished(1L);
    TestObservationRegistryAssert.assertThat(observationRegistry)
        .hasObservationWithNameEqualTo("enrollment.enroll")
        .that()
        .hasBeenStarted()
        .hasBeenStopped();
  }
}
