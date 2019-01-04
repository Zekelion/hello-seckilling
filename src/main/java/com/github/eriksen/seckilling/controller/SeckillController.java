package com.github.eriksen.seckilling.controller;

import javax.annotation.Resource;
import javax.validation.Valid;

import com.github.eriksen.seckilling.annotations.EnableForSeckill;
import com.github.eriksen.seckilling.aop.EnableForSeckillAspect;
import com.github.eriksen.seckilling.dto.ActivityInitBody;
import com.github.eriksen.seckilling.dto.SeckillInfo;
import com.github.eriksen.seckilling.model.Activity;
import com.github.eriksen.seckilling.peresistence.model.ActivityCache;
import com.github.eriksen.seckilling.service.SeckillSvc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * SeckillController
 */
@RestController
@Slf4j
public class SeckillController {
  @Resource
  private SeckillSvc seckillSvc;

  @PostMapping("/api/v1.0/seckill/init")
  @ResponseStatus(HttpStatus.CREATED)
  public Activity createSeckillActivity(@Valid @RequestBody ActivityInitBody body) {
    return seckillSvc.createSeckillActivity(body);
  }

  @EnableForSeckill()
  @GetMapping("/api/v1.0/seckill/info")
  public SeckillInfo getSeckillInfo(@RequestParam(value = "id") String activityId) {
    ActivityCache cache = EnableForSeckillAspect.activityCacheThreadLocal.get();

    return new SeckillInfo(
        cache.getId(),
        cache.getStartTime(),
        cache.getEndTime(),
        new Date()
    );
  }
}