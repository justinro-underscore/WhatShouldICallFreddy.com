package com.justin.whatshouldicallfreddy.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class DogPictureAlreadyExistsAdvice {
  @ExceptionHandler(DogPictureAlreadyExistsException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ResponseBody
  String dogPictureAlreadyExistsHandler(DogPictureAlreadyExistsException ex) {
    return ex.getMessage();
  }
}