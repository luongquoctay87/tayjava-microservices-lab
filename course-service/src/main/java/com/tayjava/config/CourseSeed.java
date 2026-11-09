package com.tayjava.config;

import com.tayjava.entity.Course;
import com.tayjava.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Seeds the demo catalog once. An empty database gets ids 1, 2, and 3. */
@Configuration
@RequiredArgsConstructor
public class CourseSeed {

  private final CourseRepository courseRepository;

  @Bean
  ApplicationRunner seedCourses() {
    return args -> {
      if (courseRepository.count() > 0) {
        return;
      }
      courseRepository.save(course("java-core", "Java Core from beginner to senior", true));
      courseRepository.save(course("spring-security", "Spring Security in practice", true));
      courseRepository.save(course("kafka-draft", "Kafka basics (draft)", false));
    };
  }

  private static Course course(String slug, String title, boolean published) {
    return Course.builder()
        .slug(slug)
        .title(title)
        .instructorUsername("bao.teacher")
        .published(published)
        .build();
  }
}
