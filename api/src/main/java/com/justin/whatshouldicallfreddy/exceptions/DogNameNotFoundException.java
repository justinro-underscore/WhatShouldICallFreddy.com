package com.justin.whatshouldicallfreddy.exceptions;

public class DogNameNotFoundException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public DogNameNotFoundException(Long id) {
    super("Could not find dog name with ID " + id);
  }
}