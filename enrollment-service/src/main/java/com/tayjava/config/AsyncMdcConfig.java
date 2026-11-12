package com.tayjava.config;

import java.util.Map;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;

@Configuration
public class AsyncMdcConfig {

  @Bean
  TaskDecorator mdcTaskDecorator() {
    return task -> {
      Map<String, String> captured = MDC.getCopyOfContextMap();
      return () -> {
        Map<String, String> previous = MDC.getCopyOfContextMap();
        if (captured != null) {
          MDC.setContextMap(captured);
        } else {
          MDC.clear();
        }
        try {
          task.run();
        } finally {
          if (previous != null) {
            MDC.setContextMap(previous);
          } else {
            MDC.clear();
          }
        }
      };
    };
  }
}
