package com.github.eriksen.seckilling.service.impl;

import java.util.*;

import com.github.eriksen.seckilling.dto.ProductInventoryDto;
import com.github.eriksen.seckilling.model.Product;
import com.github.eriksen.seckilling.peresistence.model.ProductCache;
import com.github.eriksen.seckilling.peresistence.repository.ProductCacheRepo;
import com.github.eriksen.seckilling.repository.ProductRepo;
import com.github.eriksen.seckilling.service.ProductSvc;

import com.github.eriksen.seckilling.utils.InventoryConst;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

/**
 * ProductSvcImpl
 */
@Service
@Slf4j
public class ProductSvcImpl implements ProductSvc {

  @Data
  private static class ProductInventoryStats {
    private int inventory;
    private String id;
  }

  @Autowired
  private ProductRepo productRepo;

  @Autowired
  private ProductCacheRepo productCacheRepo;

  @Autowired
  @Qualifier("seckillDbTemp")
  private MongoTemplate mongoTemplate;

  @Override
  public Optional<Product> getById(String id) {
    Optional<ProductCache> cache = productCacheRepo.findById(id);
    if (cache.isPresent()) {
      return Optional.of(cache.get().getProduct());
    }

    return productRepo.findById(id);
  }

  @Override
  public Page<Product> getPage(int skip, int limit, Sort sort) {
    Pageable page = PageRequest.of(skip, limit, sort);
    return productRepo.findAll(page);
  }

  @Override
  public Map<String, Integer> getProductsInventory(List<ObjectId> ids) {
    Map<String, Integer> result = new HashMap<>();

    Aggregation pipeline = newAggregation(
        match(Criteria.where("pId").in(ids)),
        project("pId")
            .and(ConditionalOperators.when(
                ComparisonOperators.valueOf("direction").equalToValue(InventoryConst.Direction.OUT.toString()))
                .thenValueOf(ArithmeticOperators.Multiply.valueOf("count").multiplyBy(-1))
                .otherwiseValueOf("count")).as("count"),
        group("pId").sum("count").as("count"),
        project()
            .and("count").as("inventory")
            .and("_id").as("id")
    );

    log.debug("[Pipeline] " + pipeline);
    List<ProductInventoryStats> aggreRes = mongoTemplate.aggregate(pipeline, "product.inventory", ProductInventoryStats.class).getMappedResults();
    log.debug("[Result] " + aggreRes);
    for (ProductInventoryStats item : aggreRes) {
      result.put(item.getId(), item.getInventory());
    }

    return result;
  }

  @Override
  public ProductInventoryDto getProductInventory(ObjectId id) {
    Optional<ProductCache> productCache = productCacheRepo.findById(id.toString());
    ProductInventoryDto result = new ProductInventoryDto();
    if (productCache.isPresent()) {
      result.setId(productCache.get().getId());
      result.setInventory(productCache.get().getInventory());
      return result;
    }

    Map<String, Integer> inventoryStats = this.getProductsInventory(Collections.singletonList(id));
    // inventoryStats.size == 1
    for (Map.Entry<String, Integer> entry : inventoryStats.entrySet()) {
      result.setId(entry.getKey());
      result.setInventory(entry.getValue());
    }

    return result;
  }
}