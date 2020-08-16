package com.justin.whatshouldicallfreddy.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class DogPictureNotFoundAdvice {
  @ExceptionHandler(DogPictureNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  String dogPictureNotFoundHandler(DogPictureNotFoundException ex) {
    return ex.getMessage();
  }
}