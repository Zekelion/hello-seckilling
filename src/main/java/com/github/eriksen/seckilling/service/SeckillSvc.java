package com.github.eriksen.seckilling.service;

import com.github.eriksen.seckilling.dto.ActivityInitBody;
import com.github.eriksen.seckilling.dto.SeckillOrderBody;
import com.github.eriksen.seckilling.model.Activity;
import com.github.eriksen.seckilling.model.Order;
import com.github.eriksen.seckilling.utils.CustomException;
import org.bson.types.ObjectId;

import java.util.Map;

/**
 * SeckillSvc
 */
public interface SeckillSvc {

  Activity createSeckillActivity(ActivityInitBody body) throws CustomException;

  boolean preheatSeckillActivity(Activity activity);

  /**
   * @param activityId activity id
   * @param products product's id array
   * @param userId user id
   * @return Order
   */
  Order createSeckillOrder(String activityId, SeckillOrderBody.Product[] products, String userId);
}