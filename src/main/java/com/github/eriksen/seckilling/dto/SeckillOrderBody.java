package com.github.eriksen.seckilling.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class SeckillOrderBody {
  @Data
  public static class Product {
    private String id;
    private int count;
  }

  @NotEmpty
  private Product[] products;
}
