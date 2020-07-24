package com.justin.whatshouldicallfreddy.exceptions;

public class DogPictureNotFoundException extends RuntimeException {
  public DogPictureNotFoundException(Long id) {
    super("Could not find dog picture " + id);
  }
}