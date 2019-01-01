package com.github.eriksen.seckilling.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ProductSnapshot
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSnapshot {

  private String pId;

  private String name;

  private int price; // count in cents

  private int inventory;

  private Date createdTime; // product createdTime

  private Date lastModTime; // product lasModTime

  private Date timestamp; // snapshot createdTime
}