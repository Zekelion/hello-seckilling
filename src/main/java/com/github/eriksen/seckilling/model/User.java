package com.github.eriksen.seckilling.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * User
 */
@Data
@Document(collection = "user")
public class User {
  @Id
  private String id;

  private String name;

  private Date createdTime;

  private Date lastModTime;
}