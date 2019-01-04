package com.github.eriksen.seckilling.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.github.eriksen.seckilling.dto.ActivityInitBody;
import com.github.eriksen.seckilling.messages.KafkaSender;
import com.github.eriksen.seckilling.model.Activity;
import com.github.eriksen.seckilling.model.Product;
import com.github.eriksen.seckilling.model.ProductInventory;
import com.github.eriksen.seckilling.peresistence.model.ActivityCache;
import com.github.eriksen.seckilling.peresistence.model.ProductCache;
import com.github.eriksen.seckilling.peresistence.repository.ActivityCacheRepo;
import com.github.eriksen.seckilling.peresistence.repository.ProductCacheRepo;
import com.github.eriksen.seckilling.repository.ActivityRepo;
import com.github.eriksen.seckilling.repository.ProductInventoryRepo;
import com.github.eriksen.seckilling.repository.ProductRepo;
import com.github.eriksen.seckilling.service.ProductSvc;
import com.github.eriksen.seckilling.service.SeckillSvc;
import com.github.eriksen.seckilling.utils.CustomException;
import com.github.eriksen.seckilling.utils.InventoryConst;
import com.github.eriksen.seckilling.utils.MQConst;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

/**
 * SeckillSvcImpl
 */
@Service
@Slf4j
public class SeckillSvcImpl implements SeckillSvc {

  @Autowired
  private ActivityRepo activityRepo;
  @Resource
  private ProductSvc productSvc;
  @Autowired
  private KafkaSender kafkaSender;
  @Autowired
  private ProductInventoryRepo productInventoryRepo;
  @Autowired
  private ActivityCacheRepo activityCacheRepo;
  @Autowired
  private ProductRepo productRepo;
  @Autowired
  private ProductCacheRepo productCacheRepo;

  @Override
  public Activity createSeckillActivity(ActivityInitBody body) throws CustomException {
    Page<Product> page = productSvc.getPage(0, body.getProdNum(), new Sort(Direction.DESC, "createdTime"));

    if (page.isEmpty() || page.getTotalElements() < body.getProdNum()) {
      throw new CustomException("E_INIT_SECKILL_SVC_039", HttpStatus.NOT_ACCEPTABLE_406, "products not exists");
    }

    List<ObjectId> prodList = new ArrayList<>();
    List<ProductInventory> inventoryList = new ArrayList<>();
    for (Product item : page.getContent()) {
      prodList.add(new ObjectId(item.getId()));

      ProductInventory payload = new ProductInventory();
      payload.setPId(new ObjectId(item.getId()));
      payload.setCount(100);
      payload.setDirection(InventoryConst.Direction.IN.toString());
      inventoryList.add(payload);
    }

    // init product inventory 100
    productInventoryRepo.saveAll(inventoryList);

    Activity activity = new Activity();
    activity.setStartTime(body.getStartTime());
    activity.setEndTime(body.getEndTime());
    activity.setProducts(prodList);

    Activity result = activityRepo.save(activity);

    // send activity info to mq
    kafkaSender.sendToKafka(MQConst.SECKILL_ACTIVITY_TOPIC, result);

    return result;
  }

  @Override
  public boolean preheatSeckillActivity(Activity activity) {
    ActivityCache activityCache = new ActivityCache();
    activity.setId(activity.getId());
    activityCache.setStartTime(activity.getStartTime());
    activityCache.setEndTime(activity.getEndTime());

    activityCacheRepo.save(activityCache); // cache activity info

    List<Product> products = productRepo.findAllByIds(activity.getProducts());
    Map<String, Integer> inventories = productSvc.getProductsInventory(activity.getProducts());

    List<ProductCache> productCaches = new ArrayList<>();
    for (Product product : products) {
      ProductCache prodCache = new ProductCache();
      prodCache.setId(product.getId());
      prodCache.setProduct(product);

      if (!inventories.containsKey(product.getId().toString())) {
        throw new CustomException(
            "E_SECKILL_PREHEAT_SVC_116",
            HttpStatus.INTERNAL_SERVER_ERROR_500,
            "Product inventory not satisfied" + product.getId().toString()
        );
      }

      prodCache.setInventory(inventories.get(product.getId().toString()));

      log.debug("Cache product" + prodCache);
      productCaches.add(prodCache);
    }

    productCacheRepo.saveAll(productCaches); // cache product info

    return true;
  }
}