package com.github.eriksen.seckilling.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.eriksen.seckilling.dto.CommonException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * CustomExceptionHandler
 */
@ControllerAdvice(basePackages = "com.github.eriksen.seckilling.controller", annotations = { RestController.class })
public class CustomExceptionHandler {

  private Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

  @ExceptionHandler({ CustomException.class })
  @ResponseBody
  public CommonException exception(HttpServletRequest request, HttpServletResponse response, CustomException e) {
    logger.error("[Error] " + request.getRequestURI() + " " + e.getReason());
    e.printStackTrace();

    CommonException resError = new CommonException();
    response.setStatus(e.getHttpStatus());
    resError.setError(e.getErrorCode());
    resError.setStatus(e.getHttpStatus());
    resError.setMessage(e.getReason());

    return resError;
  }
}