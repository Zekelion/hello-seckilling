package com.github.eriksen.seckilling.service.impl;

import java.util.*;

import javax.annotation.Resource;

import com.github.eriksen.seckilling.dto.ActivityInitBody;
import com.github.eriksen.seckilling.dto.SeckillOrderBody;
import com.github.eriksen.seckilling.messages.KafkaSender;
import com.github.eriksen.seckilling.model.*;
import com.github.eriksen.seckilling.peresistence.model.ActivityCache;
import com.github.eriksen.seckilling.peresistence.model.ProductCache;
import com.github.eriksen.seckilling.peresistence.repository.ActivityCacheRepo;
import com.github.eriksen.seckilling.peresistence.repository.ProductCacheRepo;
import com.github.eriksen.seckilling.repository.*;
import com.github.eriksen.seckilling.service.ProductSvc;
import com.github.eriksen.seckilling.service.SeckillSvc;
import com.github.eriksen.seckilling.utils.CustomException;
import com.github.eriksen.seckilling.utils.InventoryConst;
import com.github.eriksen.seckilling.utils.MQConst;

import com.github.eriksen.seckilling.utils.OrderConst;
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
  @Autowired
  private OrderRepo orderRepo;
  @Autowired
  private OrderDetailRepo orderDetailRepo;

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
    activityCache.setId(activity.getId());
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

  @Override
  public Order createSeckillOrder(String activityId, SeckillOrderBody.Product[] products, String userId) throws RuntimeException {
    Order order = null;

    try {
      int totalPrice = 0;
      List<Product> snapshots = new ArrayList<>();
      for (SeckillOrderBody.Product product : products) {
        Optional<ProductCache> productCache = productCacheRepo.findById(product.getId());
        if (!productCache.isPresent()) {
          throw new CustomException(
              "E_SECKILL_ORDER_SVC_154",
              HttpStatus.NOT_FOUND_404,
              "商品信息不存在"
          );
        }

        snapshots.add(productCache.get().getProduct());
        totalPrice += productCache.get().getProduct().getPrice() * product.getCount();
      }


      Order orderPayload = new Order();
      orderPayload.setAId(new ObjectId(activityId));
      orderPayload.setUId(new ObjectId(userId));
      orderPayload.setStatus(OrderConst.OrderStatus.NEW);
      orderPayload.setTotalPrice(totalPrice);

      order = orderRepo.insert(orderPayload);

      List<OrderDetail> orderDetailPayloads = new ArrayList<>();
      for (int i = 0; i < products.length; i++) {
        OrderDetail detail = new OrderDetail();
        detail.setOId(new ObjectId(order.getId()));
        detail.setPId(new ObjectId(products[i].getId()));
        detail.setCount(products[i].getCount());
        detail.setProductSnapshot(snapshots.get(i));

        orderDetailPayloads.add(detail);
      }

      orderDetailRepo.insert(orderDetailPayloads);

      return order;
    } catch (Throwable e) {
      log.error("[Exit](error) " + e.getMessage());
      if (!(e instanceof CustomException)) {
        e.printStackTrace();
      }

      // rollback : cancel order
      if (order != null) {
        order.setStatus(OrderConst.OrderStatus.CANCELLED);
        order.setLastModTime(new Date());
        orderRepo.save(order);
      }

      throw e;
    }
  }
}