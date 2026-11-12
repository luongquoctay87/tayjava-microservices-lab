package com.tayjava.demo.monolith;

import com.tayjava.common.dto.ApiResp;
import com.tayjava.common.dto.Resp;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
public class MonolithCourseController {

  @GetMapping
  public ResponseEntity<Resp<List<Map<String, String>>>> list() {
    return ApiResp.ok(
        List.of(
            Map.of(
                "slug", "java-core",
                "title", "Java Core (monolith)",
                "servedBy", "monolith")));
  }
}
