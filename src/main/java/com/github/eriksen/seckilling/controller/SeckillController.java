package com.github.eriksen.seckilling.controller;

import javax.annotation.Resource;
import javax.validation.Valid;

import com.github.eriksen.seckilling.dto.ActivityInitBody;
import com.github.eriksen.seckilling.model.Activity;
import com.github.eriksen.seckilling.service.SeckillSvc;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * SeckillController
 */
@RestController
public class SeckillController {
  @Resource
  private SeckillSvc seckillSvc;

  @PostMapping("/api/v1.0/seckill/init")
  @ResponseStatus(HttpStatus.CREATED)
  public Activity createSeckillActivity(@Valid @RequestBody ActivityInitBody body) {
    Activity result = seckillSvc.createSeckillActivity(body);
    return result;
  }
}