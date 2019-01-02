package com.github.eriksen.seckilling.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * CustomException
 */
@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
public class CustomException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  private String errorCode;

  private int httpStatus;

  private String reason;
}