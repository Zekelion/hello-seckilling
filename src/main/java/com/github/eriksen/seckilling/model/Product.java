package com.github.eriksen.seckilling.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * Product
 */
@Data
@Document(collection="product")
public class Product {
  @Id
  private String id;

  private String name;

  private int price; // count in cents

  private Date createdTime = new Date();

  private Date lastModTime = new Date();
}