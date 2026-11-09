package com.tayjava.repository;

import com.tayjava.entity.Enrollment;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

/** In-memory enrollment store. Lesson 7 replaces this with a database per service. */
@Repository
public class EnrollmentRepository {

  private final ConcurrentHashMap<Long, Enrollment> enrollments = new ConcurrentHashMap<>();
  private final AtomicLong seq = new AtomicLong(1);

  public List<Enrollment> findByStudentUsername(String studentUsername) {
    return enrollments.values().stream()
        .filter(enrollment -> enrollment.getStudentUsername().equals(studentUsername))
        .toList();
  }

  public Enrollment save(Enrollment enrollment) {
    if (enrollment.getId() == null) {
      enrollment.setId(seq.getAndIncrement());
    }
    enrollments.put(enrollment.getId(), enrollment);
    return enrollment;
  }
}
