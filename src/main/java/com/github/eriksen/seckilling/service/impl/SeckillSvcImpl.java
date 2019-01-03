package com.github.eriksen.seckilling.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.github.eriksen.seckilling.dto.ActivityInitBody;
import com.github.eriksen.seckilling.messages.KafkaSender;
import com.github.eriksen.seckilling.model.Activity;
import com.github.eriksen.seckilling.model.Product;
import com.github.eriksen.seckilling.model.ProductInventory;
import com.github.eriksen.seckilling.repository.ActivityRepo;
import com.github.eriksen.seckilling.repository.ProductInventoryRepo;
import com.github.eriksen.seckilling.service.ProductSvc;
import com.github.eriksen.seckilling.service.SeckillSvc;
import com.github.eriksen.seckilling.utils.CustomException;
import com.github.eriksen.seckilling.utils.InventoryConst;
import com.github.eriksen.seckilling.utils.MQConst;

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
public class SeckillSvcImpl implements SeckillSvc {

  @Autowired
  private ActivityRepo activityRepo;
  @Resource
  private ProductSvc productSvc;
  @Autowired
  private KafkaSender kafkaSender;
  @Autowired
  private ProductInventoryRepo productInventoryRepo;

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
}