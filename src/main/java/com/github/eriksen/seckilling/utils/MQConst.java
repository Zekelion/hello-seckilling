package com.github.eriksen.seckilling.utils;

/**
 * MQConst
 */
public class MQConst {

  public static final String SECKILL_ACTIVITY_TOPIC = "seckill-activity";

  public static final String SECKILL_ORDER_CREATE_TOPIC = "seckill-order-create";

  public static final String SECKILL_CONSUMER_GROUP = "seckill";

  public static final String SECKILL_BROADCAST_CONSUMER_GROUP = "seckill-01"; // groupid for consume in broadcast way, present instance 01
}