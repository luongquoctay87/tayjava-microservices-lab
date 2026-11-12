package com.tayjava.repository;

import com.tayjava.entity.CourseOrder;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseOrderRepository extends JpaRepository<CourseOrder, Long> {

  Optional<CourseOrder> findByEnrollmentId(Long enrollmentId);

  List<CourseOrder> findByStudentUsername(String studentUsername);

  List<CourseOrder> findByStudentUsernameAndStatus(String studentUsername, String status);

  List<CourseOrder> findByStatus(String status);

  Optional<CourseOrder> findFirstByStudentUsernameAndCourseIdAndStatus(
      String studentUsername, Long courseId, String status);
}
