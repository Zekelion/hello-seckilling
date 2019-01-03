package com.github.eriksen.seckilling.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

/**
 * KafkaConf
 */
@Configuration
public class KafkaProducerConf {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers = "localhost:9092";
  @Value("${spring.kafka.producer.retries}")
  private int retries = 10;
  @Value("${spring.kafka.max-block-ms}")
  private int maxBlockMs = 6000;

  @Bean
  public Map<String, Object> producerConf() {
    Map<String, Object> m = new HashMap<>();
    m.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    m.put(ProducerConfig.RETRIES_CONFIG, retries);
    m.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, maxBlockMs);
    m.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    m.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

    return m;
  }

  @Bean
  public ProducerFactory<String, Object> producerFactory() {
    return new DefaultKafkaProducerFactory<>(producerConf());
  }

  @Bean
  public KafkaTemplate<String, Object> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }
}