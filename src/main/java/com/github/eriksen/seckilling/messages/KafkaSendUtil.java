package com.github.eriksen.seckilling.messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

/**
 * KafkaSendUtil
 */
@Component
public class KafkaSendUtil {

  private static Logger logger = LoggerFactory.getLogger(KafkaSendUtil.class);

  @Autowired
  private KafkaTemplate<String, Object> kafkaTemplate;

  public boolean sendToKafka(String topic, Object data) {
    try {
      logger.debug("[Enter]");
      SendResult<String, Object> result = kafkaTemplate.send(topic, data).get();
      logger.debug("[Exit](success) " + result);
      return true;
    } catch (Exception e) {
      logger.error("[Error] Failed to send msg, due to " + e.getMessage());
      e.printStackTrace();
      return false;
    }

  }
}