package com.github.eriksen.seckilling.service;

import java.util.Optional;

import com.github.eriksen.seckilling.model.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

/**
 * ProductSvc
 */
public interface ProductSvc {

  /**
   * retrieve info vid id
   * @return Product
   */
  Optional<Product> getById(String id);

  /**
   * retrieve prod page
   * @return
   */
  Page<Product> getPage(int skip, int limit, Sort sort);
}