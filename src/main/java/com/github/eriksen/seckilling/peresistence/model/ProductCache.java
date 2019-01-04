package com.github.eriksen.seckilling.peresistence.model;

import com.github.eriksen.seckilling.model.Product;
import com.github.eriksen.seckilling.utils.CacheConst;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Data;

/**
 * ProductCache
 */
@Data
@RedisHash(CacheConst.PRODUCT_INFO_CACHE_KEY_PREFIX)
public class ProductCache {

  @Id
  private String id;

  private Product product;

  private int inventory;
}