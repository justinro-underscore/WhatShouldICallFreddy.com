package com.justin.whatshouldicallfreddy.exceptions;

public class DogPictureAlreadyExistsException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public DogPictureAlreadyExistsException(Long id) {
    super("Dog Picture with ID " + id + " already has a picture");
  }
}