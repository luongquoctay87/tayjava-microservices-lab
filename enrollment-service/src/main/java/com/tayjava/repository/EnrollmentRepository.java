package com.tayjava.repository;

import com.tayjava.entity.Enrollment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

  List<Enrollment> findByStudentUsername(String studentUsername);
}
