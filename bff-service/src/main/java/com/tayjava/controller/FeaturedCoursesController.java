package com.tayjava.controller;

import com.tayjava.common.dto.ApiResp;
import com.tayjava.common.dto.Resp;
import com.tayjava.common.dto.bff.MyCourseResp;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Demo catalogue highlight used by the Gateway SetPath featured route. */
@RestController
@RequestMapping("/api/v1/featured-courses")
public class FeaturedCoursesController {

  @GetMapping
  public ResponseEntity<Resp<List<MyCourseResp>>> featured() {
    return ApiResp.ok(List.of());
  }
}
