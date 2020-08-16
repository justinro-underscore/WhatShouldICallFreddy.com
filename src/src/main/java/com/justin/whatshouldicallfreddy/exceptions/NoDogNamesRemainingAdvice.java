package com.justin.whatshouldicallfreddy.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class NoDogNamesRemainingAdvice {
  @ExceptionHandler(NoDogNamesRemainingException.class)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  String dogNameExistsHandler(NoDogNamesRemainingException ex) {
    return ex.getMessage();
  }
}