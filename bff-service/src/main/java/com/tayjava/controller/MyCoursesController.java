package com.tayjava.controller;

import com.tayjava.common.dto.ApiResp;
import com.tayjava.common.dto.Resp;
import com.tayjava.common.dto.bff.MyCourseResp;
import com.tayjava.service.MyCoursesService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/me/courses")
public class MyCoursesController {

  private final MyCoursesService myCoursesService;

  @GetMapping
  public ResponseEntity<Resp<List<MyCourseResp>>> myCourses(
      @RequestParam("student") String studentUsername) {
    // Stand-in for the authenticated student until lessons 28–29.
    return ApiResp.ok(myCoursesService.listMyCourses(studentUsername));
  }
}
