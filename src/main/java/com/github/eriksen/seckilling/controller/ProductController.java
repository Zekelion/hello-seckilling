package com.github.eriksen.seckilling.controller;

import java.util.Optional;

import javax.annotation.Resource;

import com.github.eriksen.seckilling.dto.CommonPage;
import com.github.eriksen.seckilling.model.Product;
import com.github.eriksen.seckilling.service.ProductSvc;

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
  public Product getProductDetail(@RequestParam(value = "id", required = true) String id) {
    Optional<Product> product = productSvc.getById(id);
    if (product.isPresent()) {
      return product.get();
    }

    return null;
  }

  @GetMapping("/api/v1.0/product/list")
  public CommonPage<Product> getProductList(@RequestParam(value = "skip", defaultValue = "0") String skip,
      @RequestParam(value = "limit", defaultValue = "10") String limit) {
    int _skip = Integer.parseInt(skip);
    int _limit = Integer.parseInt(limit);
    Sort sort = new Sort(Direction.DESC, "createdTime");
    Page<Product> page = productSvc.getPage(_skip, _limit, sort);

    CommonPage<Product> result = new CommonPage<Product>(page.getContent(), page.getTotalElements(), _skip, _limit);

    return result;
  }
}