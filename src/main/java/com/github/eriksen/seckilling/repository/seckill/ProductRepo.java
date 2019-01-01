package com.github.eriksen.seckilling.repository.seckill;

import com.github.eriksen.seckilling.model.Product;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * ProductRepo
 */
public interface ProductRepo extends MongoRepository<Product, String> {

}