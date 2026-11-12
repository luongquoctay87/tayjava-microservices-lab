package com.tayjava.controller;

import com.tayjava.common.dto.ApiResp;
import com.tayjava.common.dto.Resp;
import com.tayjava.common.dto.training.course.CourseResp;
import com.tayjava.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Service-to-service catalogue read. Not routed by the Gateway. Requires demo Bearer with {@code
 * scope=course.read} (lesson 29 bridge).
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/courses")
public class InternalCourseController {

  private final CourseService courseService;

  @GetMapping("/{id}")
  public ResponseEntity<Resp<CourseResp>> getById(@PathVariable Long id) {
    return ApiResp.ok(courseService.getById(id));
  }
}
