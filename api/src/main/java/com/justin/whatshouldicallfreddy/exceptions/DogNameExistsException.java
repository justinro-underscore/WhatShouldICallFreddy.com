package com.justin.whatshouldicallfreddy.exceptions;

public class DogNameExistsException extends RuntimeException {
  public DogNameExistsException(String name) {
    super("Dog name \"" + name + "\" has already been submitted");
  }
}