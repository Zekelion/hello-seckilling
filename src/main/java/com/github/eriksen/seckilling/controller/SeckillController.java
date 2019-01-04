package com.github.eriksen.seckilling.controller;

import javax.annotation.Resource;
import javax.validation.Valid;

import com.github.eriksen.seckilling.annotations.EnableForSeckill;
import com.github.eriksen.seckilling.dto.ActivityInitBody;
import com.github.eriksen.seckilling.dto.SeckillInfo;
import com.github.eriksen.seckilling.model.Activity;
import com.github.eriksen.seckilling.service.SeckillSvc;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

  @GetMapping("/api/v1.0/seckill/info")
  @EnableForSeckill(enable = false)
  public SeckillInfo getSeckillInfo(@RequestParam(value = "id") String id) {
    return null;
  }
}