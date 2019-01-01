package com.github.eriksen.seckilling.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.eriksen.seckilling.model.User;
import com.github.eriksen.seckilling.utils.HttpConst;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * UserCtxInteceptor
 */
@Component
public class UserCtxInteceptor implements HandlerInterceptor {

  private static Logger logger = LoggerFactory.getLogger(UserCtxInteceptor.class);

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    User user = new User();
    user.setId("5c2b7097cfc4732a018fa5eb");
    user.setName("测试用户");

    request.setAttribute(HttpConst.USER_CTX_KEY, user);
    logger.debug("[User] " + request.getAttribute(HttpConst.USER_CTX_KEY));
    return true;
  }
}