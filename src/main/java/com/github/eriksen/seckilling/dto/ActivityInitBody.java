package com.github.eriksen.seckilling.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

import lombok.Data;

/**
 * ActivityInitBody
 */
@Data
public class ActivityInitBody {

  @NotNull(message = "日期不能为空")
  private Date startTime;

  @NotNull(message = "日期不能为空")
  private Date endTime;

  @Range(min = 1)
  private int prodNum; // product nums
}