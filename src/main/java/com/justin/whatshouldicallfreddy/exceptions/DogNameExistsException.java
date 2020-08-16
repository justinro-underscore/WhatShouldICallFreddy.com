package com.justin.whatshouldicallfreddy.exceptions;

public class DogNameExistsException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public DogNameExistsException(String name) {
    super("Dog name \"" + name + "\" has already been submitted");
  }
}