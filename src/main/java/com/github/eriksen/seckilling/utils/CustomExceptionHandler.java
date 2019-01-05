package com.github.eriksen.seckilling.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.eriksen.seckilling.dto.CommonException;

import org.eclipse.jetty.http.HttpStatus;
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

  @ExceptionHandler({ CustomException.class})
  @ResponseBody
  public CommonException exception(HttpServletRequest request, HttpServletResponse response, CustomException e) {
    logger.error("[Error] " + request.getRequestURI() + " " + e.getMessage());

    CommonException resError = new CommonException();
    resError.setStatus(e.getHttpStatus());
    resError.setError(e.getErrorCode());
    resError.setMessage(e.getMessage());

    response.setStatus(e.getHttpStatus());

    return resError;
  }

  @ExceptionHandler({ RuntimeException.class})
  @ResponseBody
  public CommonException runtimeException(HttpServletRequest request, HttpServletResponse response, RuntimeException e) {
    if (e instanceof CustomException) {
      return exception(request, response, (CustomException) e);
    }

    logger.error("[Error] " + request.getRequestURI() + " " + e.getMessage());
    e.printStackTrace();

    CommonException resError = new CommonException();
    resError.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
    resError.setError("INTERNAL_ERROR");
    resError.setMessage(e.getMessage());

    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);

    return resError;
  }
}