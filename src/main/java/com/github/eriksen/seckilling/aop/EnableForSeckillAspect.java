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
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Aspect
@Component
@Slf4j
public class EnableForSeckillAspect {

  @Autowired
  private ActivityCacheRepo activityCacheRepo;

  private static final String PARAM_ID = "activityId";

  public static ThreadLocal<ActivityCache> activityCacheThreadLocal = new ThreadLocal<>();

  @Pointcut("within(com.github.eriksen.seckilling.controller.*) && @annotation(com.github.eriksen.seckilling.annotations.EnableForSeckill)")
  public void enter() {
  }

  @Around("enter()")
  public Object aroundEnter(ProceedingJoinPoint pjp) throws RuntimeException {
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

      Parameter[] methodParams = method.getParameters();
      int argIndex = -1;
      for (int i = 0; i < methodParams.length; i++) {
        if (methodParams[i].getName().equals(PARAM_ID)) {
          argIndex = i;
          break;
        }
      }

      if (argIndex < 0) {
        throw new CustomException(
            "E_SECKILL_ENABLE_064",
            HttpStatus.NOT_ACCEPTABLE_406,
            "请传入活动id"
        );
      }

      Object[] args = pjp.getArgs();
      String activityId = (String) args[argIndex];
      Optional<ActivityCache> cache = activityCacheRepo.findById(activityId);
      log.debug("[Cache] " + cache);

      LocalDateTime now = LocalDateTime.now();
      if (!cache.isPresent()
          || !now.isAfter(cache.get().getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
          || !now.isBefore(cache.get().getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())) {
        throw new CustomException(
            "E_SECKILL_ENABLE_074",
            HttpStatus.BAD_REQUEST_400,
            "未在活动时间或活动未启用"
        );
      }

      activityCacheThreadLocal.set(cache.get());

      return pjp.proceed(args);
    } catch (Throwable e) {
      log.error("[Exit](error) " + e.getMessage());
      e.printStackTrace();
      throw (RuntimeException) e;
    }
  }
}
