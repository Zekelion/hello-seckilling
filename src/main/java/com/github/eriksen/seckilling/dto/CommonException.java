package com.github.eriksen.seckilling.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CommonException
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonException {
  private String error;

  private String message;

  private int status;
}