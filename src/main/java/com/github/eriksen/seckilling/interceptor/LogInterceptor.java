package com.github.eriksen.seckilling.interceptor;

import java.time.Duration;
import java.time.LocalTime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * LogInterceptor
 */
@Component
public class LogInterceptor implements HandlerInterceptor {

  private static Logger logger = LoggerFactory.getLogger(LogInterceptor.class);
  private static ThreadLocal<LocalTime> entryTime = new ThreadLocal<>();

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    logger.info("[HTTP-Entry] " + request.getRequestURL());
    entryTime.set(LocalTime.now());
    return true;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
      ModelAndView modelAndView) throws Exception {
    long duration = Duration.between(entryTime.get(), LocalTime.now()).toMillis();
    logger.info("[HTTP-Exit] " + request.getRequestURL() + " [Status] " + response.getStatus() + " " + duration + "ms");
    return;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
      throws Exception {
    return;
  }
}