package com.github.eriksen.seckilling.repository;

import com.github.eriksen.seckilling.model.ProductInventory;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * ProductInventoryRepo
 */
public interface ProductInventoryRepo extends MongoRepository<ProductInventory, String>{

}