package com.tayjava.service;

import com.tayjava.common.dto.training.course.CourseReq;
import com.tayjava.common.dto.training.course.CourseResp;
import com.tayjava.common.enums.ErrorCode;
import com.tayjava.common.exception.ErrorException;
import com.tayjava.entity.Course;
import com.tayjava.repository.CourseRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "CourseService")
public class CourseServiceImpl implements CourseService {

  private final CourseRepository courseRepository;

  @Override
  @Transactional(readOnly = true)
  public List<CourseResp> listPublished() {
    return courseRepository.findByPublishedTrue().stream().map(this::toResp).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public CourseResp getById(Long id) {
    return courseRepository
        .findById(id)
        .map(this::toResp)
        .orElseThrow(() -> new ErrorException(ErrorCode.COURSE_NOT_FOUND));
  }

  @Override
  @Transactional
  public CourseResp create(CourseReq request) {
    // No Spring Security yet: open until lessons 28–29 lock this to the instructor role.
    Course created =
        courseRepository.save(
            Course.builder()
                .slug(request.getSlug())
                .title(request.getTitle())
                .instructorUsername(request.getInstructorUsername())
                .published(true)
                .build());
    log.info("Created course id={} slug={}", created.getId(), created.getSlug());
    return toResp(created);
  }

  private CourseResp toResp(Course course) {
    return CourseResp.builder()
        .id(course.getId())
        .slug(course.getSlug())
        .title(course.getTitle())
        .instructorUsername(course.getInstructorUsername())
        .published(course.isPublished())
        .build();
  }
}
