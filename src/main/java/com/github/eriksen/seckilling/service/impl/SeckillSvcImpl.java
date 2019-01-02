package com.github.eriksen.seckilling.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.github.eriksen.seckilling.dto.ActivityInitBody;
import com.github.eriksen.seckilling.model.Activity;
import com.github.eriksen.seckilling.model.Product;
import com.github.eriksen.seckilling.repository.ActivityRepo;
import com.github.eriksen.seckilling.service.ProductSvc;
import com.github.eriksen.seckilling.service.SeckillSvc;
import com.github.eriksen.seckilling.utils.CustomException;

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

  @Override
  public Activity createSeckillActivity(ActivityInitBody body) throws CustomException {
    Page<Product> page = productSvc.getPage(0, body.getProdNum(), new Sort(Direction.DESC, "createdTime"));

    if (page.isEmpty() || page.getTotalElements() < body.getProdNum()) {
      throw new CustomException("E_INIT_SECKILL_SVC_039", HttpStatus.NOT_ACCEPTABLE_406, "products not exists");
    }

    List<ObjectId> prodList = new ArrayList<>();
    for (Product item : page.getContent()) {
      prodList.add(new ObjectId(item.getId()));
    }

    Activity activity = new Activity();
    activity.setStartTime(body.getStartTime());
    activity.setEndTime(body.getEndTime());
    activity.setProducts(prodList);

    return activityRepo.save(activity);
  }
}