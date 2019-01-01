package com.github.eriksen.seckilling.model;

import java.util.Date;

import com.github.eriksen.seckilling.utils.OrderConst.OrderStatus;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * Order
 */
@Data
@Document(collection="order")
public class Order {
  @Id
  private String id;

  private int totalPrice; // count in cents

  private ObjectId uId; // order's owner

  private OrderStatus status;

  private Date createdTime = new Date();

  private Date lastModTime = new Date();

}