package com.github.eriksen.seckilling.model;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * Activity
 */
@Data
@Document(collection = "activity")
public class Activity {
  @Id
  private String id;

  private Date startTime;

  private Date endTime;

  private List<ObjectId> products;

  private Date createdTime = new Date();
}