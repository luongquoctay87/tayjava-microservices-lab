package com.tayjava.repository;

import com.tayjava.entity.Course;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

/** In-memory course store. Lesson 7 replaces this with a database per service. */
@Repository
public class CourseRepository {

  private final ConcurrentHashMap<Long, Course> courses = new ConcurrentHashMap<>();
  private final AtomicLong seq = new AtomicLong(3);

  public CourseRepository() {
    save(Course.builder()
        .id(1L)
        .slug("java-core")
        .title("Java Core from beginner to senior")
        .instructorUsername("bao.teacher")
        .published(true)
        .build());
    save(Course.builder()
        .id(2L)
        .slug("spring-security")
        .title("Spring Security in practice")
        .instructorUsername("bao.teacher")
        .published(true)
        .build());
  }

  public List<Course> findPublished() {
    return courses.values().stream().filter(Course::isPublished).toList();
  }

  public Optional<Course> findById(Long id) {
    return Optional.ofNullable(courses.get(id));
  }

  public Course save(Course course) {
    if (course.getId() == null) {
      course.setId(seq.getAndIncrement());
    }
    courses.put(course.getId(), course);
    return course;
  }
}
