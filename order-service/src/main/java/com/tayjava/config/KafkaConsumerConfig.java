package com.tayjava.config;

import com.tayjava.common.dto.event.EnrollmentCreatedEvent;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
@ConditionalOnProperty(name = "tayjava.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaConsumerConfig {

  @Bean
  ConsumerFactory<String, EnrollmentCreatedEvent> enrollmentEventConsumerFactory(
      @Value("${spring.kafka.bootstrap-servers:localhost:9092}") String bootstrapServers) {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-service");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.tayjava.common.dto.event");
    props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, EnrollmentCreatedEvent.class.getName());
    props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
    return new DefaultKafkaConsumerFactory<>(props);
  }

  @Bean
  ConcurrentKafkaListenerContainerFactory<String, EnrollmentCreatedEvent>
      kafkaListenerContainerFactory(
          ConsumerFactory<String, EnrollmentCreatedEvent> enrollmentEventConsumerFactory) {
    ConcurrentKafkaListenerContainerFactory<String, EnrollmentCreatedEvent> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(enrollmentEventConsumerFactory);
    return factory;
  }
}
