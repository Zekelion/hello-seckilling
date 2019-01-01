package com.github.eriksen.seckilling.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * Product
 */
@Data
@Document(collection = "product")
public class Product {
  @Id
  private String id;

  private String name;

  private int price; // count in cents

  @Indexed(direction = IndexDirection.DESCENDING)
  private Date createdTime = new Date();

  private Date lastModTime = new Date();
}