package com.github.eriksen.seckilling.aop;

import com.github.eriksen.seckilling.annotations.RateLimit;
import com.github.eriksen.seckilling.utils.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
@Slf4j
public class RateLimitAspect {

  private static final String TIME_KEY_PREFIX = "rateLimit:time:seckill:";
  private static final String COUNT_KEY_PREFIX = "rateLimit:count:seckill:";

  @Autowired
  private RedisTemplate<String, Long> redisTemplate;

  @Pointcut("within(com.github.eriksen.seckilling.controller.*) && @annotation(com.github.eriksen.seckilling.annotations.RateLimit)")
  public void enter() {
  }

  @Around("enter()")
  public Object aroundEnter(ProceedingJoinPoint pjp) throws RuntimeException {
    try {
      if (redisTemplate == null) {
        throw new CustomException(
            "E_LIMIT_METHOD_041",
            HttpStatus.INTERNAL_SERVER_ERROR_500,
            "internal error"
        );
      }

      MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
      Method method = methodSignature.getMethod();
      String className = pjp.getTarget().getClass().getSimpleName();
      log.debug("[Enter] triggered by " + className + "." + method.getName());

      RateLimit annotation = method.getAnnotation(RateLimit.class);
      boolean flag = annotation.enable();
      if (!flag) {
        log.debug("[Exit] limit disable");
        return pjp.proceed();
      }

      long limit = annotation.limit();
      if (limit <= 0) {
        throw new CustomException(
            "E_LIMIT_METHOD_062",
            HttpStatus.INTERNAL_SERVER_ERROR_500,
            "invalid limitation"
        );
      }

      String key = StringUtils.defaultIfEmpty(annotation.key(), className + ":" + method.getName());
      String RATE_LIMIT_TIME_KEY = TIME_KEY_PREFIX + key;
      String RATE_LIMIT_COUNT_KEY = COUNT_KEY_PREFIX + key;
      redisTemplate.setValueSerializer(new GenericToStringSerializer<>(Long.class));

      if (!redisTemplate.hasKey(RATE_LIMIT_TIME_KEY)) {
        redisTemplate.opsForValue().set(RATE_LIMIT_TIME_KEY, (long) 0, 1, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(RATE_LIMIT_COUNT_KEY, (long) 0, 2, TimeUnit.SECONDS);
      }

      if (redisTemplate.hasKey(RATE_LIMIT_COUNT_KEY) && redisTemplate.opsForValue().increment(RATE_LIMIT_COUNT_KEY, (long) 1) > limit) {
        throw new CustomException(
            "E_LIMIT_METHOD_071",
            HttpStatus.SERVICE_UNAVAILABLE_503,
            "系统暂时不可用"
        );
      }

      return pjp.proceed();
    } catch (Throwable e) {
      log.error("[Exit](error) " + e.getMessage());
      if (!(e instanceof CustomException)) {
        e.printStackTrace();
      }

      throw (RuntimeException) e;
    }
  }
}
