package com.github.eriksen.seckilling.controller;

import java.util.Optional;

import javax.annotation.Resource;

import com.github.eriksen.seckilling.annotations.RateLimit;
import com.github.eriksen.seckilling.dto.CommonPage;
import com.github.eriksen.seckilling.dto.ProductInventoryDto;
import com.github.eriksen.seckilling.model.Product;
import com.github.eriksen.seckilling.model.ProductInventory;
import com.github.eriksen.seckilling.service.ProductSvc;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ProductController
 */
@RestController
public class ProductController {
  @Resource
  private ProductSvc productSvc;

  @GetMapping("/api/v1.0/product/info")
  public Optional<Product> getProductDetail(@RequestParam(value = "id", required = true) String id) {
    return productSvc.getById(id);
  }

  @GetMapping("/api/v1.0/product/list")
  public CommonPage<Product> getProductList(@RequestParam(value = "skip", defaultValue = "0") String skip,
      @RequestParam(value = "limit", defaultValue = "10") String limit) {
    int _skip = Integer.parseInt(skip);
    int _limit = Integer.parseInt(limit);
    Sort sort = new Sort(Direction.DESC, "createdTime");
    Page<Product> page = productSvc.getPage(_skip, _limit, sort);

    return new CommonPage<Product>(page.getContent(), page.getTotalElements(), _skip, _limit);
  }

  @GetMapping("/api/v1.0/product/inventory")
  @RateLimit(limit = 2)
  public Optional<ProductInventoryDto> getProductInventory(@RequestParam("id") String id) {
    return Optional.of(productSvc.getProductInventory(new ObjectId(id)));
  }
}