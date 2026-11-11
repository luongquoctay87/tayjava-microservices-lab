package com.tayjava.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.tayjava.common.enums.ErrorCode;
import com.tayjava.common.exception.ErrorException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class ResilientCourseClientTest {

  @Autowired private CourseClient courseClient;

  @MockitoBean private CourseTransport courseTransport;

  @Test
  void retriesTransientCourseUnavailable() {
    doThrow(new ErrorException(ErrorCode.COURSE_UNAVAILABLE, "down"))
        .doNothing()
        .when(courseTransport)
        .requirePublished(1L);

    courseClient.requirePublished(1L);
    verify(courseTransport, times(2)).requirePublished(1L);
  }

  @Test
  void doesNotRetryBusinessNotEnrollable() {
    doThrow(new ErrorException(ErrorCode.COURSE_NOT_ENROLLABLE, "draft"))
        .when(courseTransport)
        .requirePublished(3L);

    ErrorException error =
        assertThrows(ErrorException.class, () -> courseClient.requirePublished(3L));
    assertEquals(ErrorCode.COURSE_NOT_ENROLLABLE, error.getErrorCode());
    verify(courseTransport, times(1)).requirePublished(3L);
  }
}
