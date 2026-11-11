package com.tayjava.config;

import com.tayjava.common.dto.event.EnrollmentCreatedEvent;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@ConditionalOnProperty(name = "tayjava.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaProducerConfig {

  @Bean
  ProducerFactory<String, EnrollmentCreatedEvent> enrollmentEventProducerFactory(
      @Value("${spring.kafka.bootstrap-servers:localhost:9092}") String bootstrapServers) {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  KafkaTemplate<String, EnrollmentCreatedEvent> enrollmentEventKafkaTemplate(
      ProducerFactory<String, EnrollmentCreatedEvent> enrollmentEventProducerFactory) {
    return new KafkaTemplate<>(enrollmentEventProducerFactory);
  }
}
