package com.github.eriksen.seckilling.service;

import com.github.eriksen.seckilling.dto.ActivityInitBody;
import com.github.eriksen.seckilling.model.Activity;
import com.github.eriksen.seckilling.utils.CustomException;

/**
 * SeckillSvc
 */
public interface SeckillSvc {

  Activity createSeckillActivity(ActivityInitBody body) throws CustomException;

  boolean preheatSeckillActivity(Activity activity);
}