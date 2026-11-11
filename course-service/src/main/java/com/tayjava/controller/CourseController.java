package com.tayjava.controller;

import com.tayjava.common.dto.ApiResp;
import com.tayjava.common.dto.Resp;
import com.tayjava.common.dto.training.course.CourseReq;
import com.tayjava.common.dto.training.course.CourseResp;
import com.tayjava.service.CourseService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/courses")
public class CourseController {

  private final CourseService courseService;

  @GetMapping
  public ResponseEntity<Resp<List<CourseResp>>> listPublished() {
    return ApiResp.ok(courseService.listPublished());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Resp<CourseResp>> getById(@PathVariable Long id) {
    return ApiResp.ok(courseService.getById(id));
  }

  @PostMapping
  public ResponseEntity<Resp<CourseResp>> create(@Valid @RequestBody CourseReq request) {
    return ApiResp.created(courseService.create(request));
  }
}
