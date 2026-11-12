package com.tayjava.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "course_orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseOrder {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** Null while the order is CREATED/FULFILLING and Enrollment has not written a row yet. */
  @Column(name = "enrollment_id", unique = true)
  private Long enrollmentId;

  @Column(name = "student_username", nullable = false, length = 100)
  private String studentUsername;

  @Column(name = "course_id", nullable = false)
  private Long courseId;

  @Column(name = "status", nullable = false, length = 40)
  private String status;

  @Column(name = "source_event_id", nullable = false, length = 64)
  private String sourceEventId;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;
}
