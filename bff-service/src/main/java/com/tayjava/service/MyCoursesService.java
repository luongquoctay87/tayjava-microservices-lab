package com.tayjava.service;

import com.tayjava.common.dto.bff.MyCourseResp;
import java.util.List;

public interface MyCoursesService {

  List<MyCourseResp> listMyCourses(String studentUsername);
}
