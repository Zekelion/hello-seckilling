package com.github.eriksen.seckilling.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeckillInfo {
  private String id;

  private Date startTime;

  private Date endTime;

  private Date currentTime;
}
