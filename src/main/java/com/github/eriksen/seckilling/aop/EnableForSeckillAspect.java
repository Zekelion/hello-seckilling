package com.github.eriksen.seckilling.aop;

import com.github.eriksen.seckilling.annotations.EnableForSeckill;
import com.github.eriksen.seckilling.peresistence.model.ActivityCache;
import com.github.eriksen.seckilling.peresistence.repository.ActivityCacheRepo;
import com.github.eriksen.seckilling.utils.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class EnableForSeckillAspect {

  @Autowired
  private ActivityCacheRepo activityCacheRepo;

  @Pointcut("within(com.github.eriksen.seckilling.controller.*) && @annotation(com.github.eriksen.seckilling.annotations.EnableForSeckill)")
  public void enter() {
  }

  @Around("enter()")
  public void aroundEnter(ProceedingJoinPoint pjp) throws RuntimeException {
    try {
      MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
      Method method = methodSignature.getMethod();
      log.debug("[Enter] triggered by " + pjp.getTarget().getClass().getSimpleName() + "." + method.getName());

      EnableForSeckill annotation = method.getAnnotation(EnableForSeckill.class);
      boolean flag = annotation.enable();

      if (!flag) {
        throw new CustomException(
            "E_SECKILL_ENABLE_042",
            HttpStatus.BAD_REQUEST_400,
            "未在活动时间或活动未启用"
        );
      }


//      ActivityCache cache = activityCacheRepo.findById();
//      pjp.getArgs();

      pjp.proceed();
    } catch (Throwable e) {
      log.error("[Exit](error) " + e.getMessage());
      e.printStackTrace();
      throw (RuntimeException) e;
    }
  }
}
