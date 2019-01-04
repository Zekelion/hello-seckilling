package com.github.eriksen.seckilling.dto;

import lombok.Data;

import java.util.Date;

@Data
public class SeckillInfo {
  private Date startTime;

  private Date endTime;

  private Date currentTime;
}
