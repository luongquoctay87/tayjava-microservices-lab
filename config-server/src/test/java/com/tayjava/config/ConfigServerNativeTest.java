package com.tayjava.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ConfigServerNativeTest {

  @Autowired private MockMvc mockMvc;

  @DynamicPropertySource
  static void configRepo(DynamicPropertyRegistry registry) {
    Path candidate = Path.of("").toAbsolutePath().getParent().resolve("tayjava-config");
    final Path repo =
        candidate.toFile().isDirectory() ? candidate : Path.of("tayjava-config").toAbsolutePath();
    registry.add("CONFIG_REPO_LOCATION", () -> "file:" + repo);
  }

  @Test
  void servesEnrollmentDockerProfile() throws Exception {
    mockMvc
        .perform(get("/enrollment-service/docker"))
        .andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("course-base-url")))
        .andExpect(content().string(org.hamcrest.Matchers.containsString("course-service:8081")));
  }

  @Test
  void healthIsUp() throws Exception {
    mockMvc
        .perform(get("/actuator/health"))
        .andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("UP")));
  }
}
