package com.github.eriksen.seckilling.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.eriksen.seckilling.model.Product;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

/**
 * ProductSvc
 */
public interface ProductSvc {

  /**
   * retrieve info vid id
   *
   * @return Product
   */
  Optional<Product> getById(String id);

  /**
   * retrieve prod page
   *
   * @return Page\<Product\>
   */
  Page<Product> getPage(int skip, int limit, Sort sort);

  /**
   * retrieve prods' inventory
   *
   * @return Map<String, Integer> K : Product id, V : inventory
   */
  Map<String, Integer> getProductsInventory(List<ObjectId> ids);
}