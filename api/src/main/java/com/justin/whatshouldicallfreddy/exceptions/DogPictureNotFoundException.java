package com.justin.whatshouldicallfreddy.exceptions;

public class DogPictureNotFoundException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public DogPictureNotFoundException(Long id) {
    super("Could not find dog picture with ID " + id);
  }
}