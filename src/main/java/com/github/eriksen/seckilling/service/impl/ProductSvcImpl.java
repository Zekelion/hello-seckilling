package com.github.eriksen.seckilling.service.impl;

import java.util.Optional;

import com.github.eriksen.seckilling.model.Product;
import com.github.eriksen.seckilling.repository.seckill.ProductRepo;
import com.github.eriksen.seckilling.service.ProductSvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * ProductSvcImpl
 */
@Service
public class ProductSvcImpl implements ProductSvc{

  @Autowired
  private ProductRepo productRepo;

  @Override
  public Optional<Product> getById(String id) {
    return productRepo.findById(id);
  }

  @Override
  public Page<Product> getPage(int skip, int limit, Sort sort) {
    Pageable page = PageRequest.of(skip, limit, sort);
    Page<Product> result = productRepo.findAll(page);
    return result;
  }
}