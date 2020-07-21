package com.justin.whatshouldicallfreddy.exceptions;

public class DogNameNotFoundException extends RuntimeException {
  public DogNameNotFoundException(Long id) {
    super("Could not find dog name " + id);
  }
}