package com.github.eriksen.seckilling.messages;

import com.github.eriksen.seckilling.model.Activity;
import com.github.eriksen.seckilling.peresistence.model.ActivityCache;
import com.github.eriksen.seckilling.peresistence.repository.ActivityCacheRepo;
import com.github.eriksen.seckilling.utils.MQConst;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * SeckillListener
 */
@Component
@Slf4j
public class SeckillListener {

  @Autowired
  private ActivityCacheRepo activityCacheRepo;

  @KafkaListener(topics = MQConst.SECKILL_ACTIVITY_TOPIC)
  public void onInit(Activity activity) {
    try {
      log.debug("[Msg] " + activity);
      ActivityCache activityCache = new ActivityCache();
      activity.setId(activity.getId());
      activityCache.setStartTime(activity.getStartTime());
      activityCache.setEndTime(activity.getEndTime());

      activityCacheRepo.save(activityCache);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}