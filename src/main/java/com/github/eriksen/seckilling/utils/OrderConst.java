package com.github.eriksen.seckilling.utils;

/**
 * OrderConst
 */
public class OrderConst {

  public enum OrderStatus {
    NEW("new"),
    FINISHED("finished"),
    CANCELLED("cancelled");

    private OrderStatus(String value) {
    }
  }
}