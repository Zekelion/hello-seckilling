package com.github.eriksen.seckilling.repository;

import com.github.eriksen.seckilling.model.Product;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * ProductRepo
 */
public interface ProductRepo extends MongoRepository<Product, String> {

  @Query("{'_id': {$in: ?0}}")
  List<Product> findAllByIds(List<ObjectId> ids);
}