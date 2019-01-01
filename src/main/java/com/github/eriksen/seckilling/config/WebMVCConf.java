package com.github.eriksen.seckilling.config;

import com.github.eriksen.seckilling.interceptor.LogInterceptor;
import com.github.eriksen.seckilling.interceptor.UserCtxInteceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMVCConf
 */
@Configuration
public class WebMVCConf implements WebMvcConfigurer {

  @Autowired
  private LogInterceptor logInterceptor;
  @Autowired
  private UserCtxInteceptor userCtxInteceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(logInterceptor);
    registry.addInterceptor(userCtxInteceptor);
  }
}