package com.tayjava.service;

import com.tayjava.cache.CourseCacheInvalidator;
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
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "CourseService")
public class CourseServiceImpl implements CourseService {

  private final CourseRepository courseRepository;
  private final CourseCacheInvalidator courseCacheInvalidator;

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
    Course created =
        courseRepository.save(
            Course.builder()
                .slug(request.getSlug())
                .title(request.getTitle())
                .instructorUsername(request.getInstructorUsername())
                .published(true)
                .build());
    log.info("Created course id={} slug={}", created.getId(), created.getSlug());
    evictAfterCommit(created.getId());
    return toResp(created);
  }

  @Override
  @Transactional
  public CourseResp update(Long id, CourseReq request) {
    Course course =
        courseRepository
            .findById(id)
            .orElseThrow(() -> new ErrorException(ErrorCode.COURSE_NOT_FOUND));
    course.setSlug(request.getSlug());
    course.setTitle(request.getTitle());
    course.setInstructorUsername(request.getInstructorUsername());
    Course saved = courseRepository.save(course);
    log.info("Updated course id={} slug={}", saved.getId(), saved.getSlug());
    evictAfterCommit(saved.getId());
    return toResp(saved);
  }

  private void evictAfterCommit(Long courseId) {
    if (!TransactionSynchronizationManager.isSynchronizationActive()) {
      courseCacheInvalidator.evict(courseId);
      return;
    }
    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCommit() {
            courseCacheInvalidator.evict(courseId);
          }
        });
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
