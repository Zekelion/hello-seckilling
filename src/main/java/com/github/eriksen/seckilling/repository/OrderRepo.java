package com.github.eriksen.seckilling.repository;

import com.github.eriksen.seckilling.model.Order;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * OrderRepo
 */
public interface OrderRepo extends MongoRepository<Order, String>{
  
}