package com.github.eriksen.seckilling.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * OrderDetail
 */
@Data
@Document(collection="order.detail")
public class OrderDetail {
  @Id
  private String id;

  @Indexed
  private ObjectId oId; // order _id
  
  private ProductInventory productSnapshot; // product snapshot

  private Date createdTime = new Date();

  private Date lastModTime = new Date();
}