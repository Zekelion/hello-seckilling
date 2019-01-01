package com.github.eriksen.seckilling.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CommonPage
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonPage<T> {

  private List<T> content;

  private long total;

  private int skip;

  private int limit;
}