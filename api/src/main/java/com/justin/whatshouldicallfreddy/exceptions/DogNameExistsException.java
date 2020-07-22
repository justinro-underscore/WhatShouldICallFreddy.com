package com.justin.whatshouldicallfreddy.exceptions;

public class DogNameExistsException extends RuntimeException {
  public DogNameExistsException(String name) {
    super("Dog with name " + name + " already exists");
  }
}