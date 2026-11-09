package com.tayjava.service;

import com.tayjava.common.dto.training.course.CourseReq;
import com.tayjava.common.dto.training.course.CourseResp;
import java.util.List;

public interface CourseService {

  List<CourseResp> listPublished();

  CourseResp getById(Long id);

  CourseResp create(CourseReq request);
}
