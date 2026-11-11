package com.tayjava.service;

import com.tayjava.common.dto.event.EnrollmentCreatedEvent;
import com.tayjava.common.dto.order.OrderResp;
import com.tayjava.entity.CourseOrder;
import com.tayjava.messaging.InboxGuard;
import com.tayjava.repository.CourseOrderRepository;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "OrderService")
public class OrderServiceImpl implements OrderService {

  private final CourseOrderRepository courseOrderRepository;
  private final InboxGuard inboxGuard;

  @Override
  @Transactional
  public OrderResp createFromEnrollmentEvent(EnrollmentCreatedEvent event) {
    if (!inboxGuard.claim(event.eventId(), "EnrollmentCreated")) {
      return courseOrderRepository
          .findByEnrollmentId(event.enrollmentId())
          .map(this::toResp)
          .orElse(null);
    }
    return courseOrderRepository
        .findByEnrollmentId(event.enrollmentId())
        .map(this::toResp)
        .orElseGet(
            () -> {
              CourseOrder saved =
                  courseOrderRepository.save(
                      CourseOrder.builder()
                          .enrollmentId(event.enrollmentId())
                          .studentUsername(event.studentUsername())
                          .courseId(event.courseId())
                          .status("CREATED")
                          .sourceEventId(event.eventId())
                          .createdAt(Instant.now())
                          .build());
              log.info(
                  "Created order id={} from enrollmentId={} eventId={}",
                  saved.getId(),
                  saved.getEnrollmentId(),
                  saved.getSourceEventId());
              return toResp(saved);
            });
  }

  @Override
  @Transactional(readOnly = true)
  public List<OrderResp> getByStudentUsername(String studentUsername) {
    return courseOrderRepository.findByStudentUsername(studentUsername).stream()
        .map(this::toResp)
        .toList();
  }

  private OrderResp toResp(CourseOrder order) {
    return OrderResp.builder()
        .id(order.getId())
        .enrollmentId(order.getEnrollmentId())
        .studentUsername(order.getStudentUsername())
        .courseId(order.getCourseId())
        .status(order.getStatus())
        .sourceEventId(order.getSourceEventId())
        .createdAt(order.getCreatedAt())
        .build();
  }
}
