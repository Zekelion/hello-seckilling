package com.github.eriksen.seckilling.messages;

import com.github.eriksen.seckilling.utils.MQConst;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * SeckillListener
 */
@Component
@Slf4j
public class SeckillListener {

  @KafkaListener(topics = MQConst.SECKILL_ACTIVITY_TOPIC)
  public void onInit(Object message) {
    try {
      log.debug("[Msg] " + message);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}