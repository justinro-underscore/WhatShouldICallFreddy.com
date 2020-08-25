package com.justin.whatshouldicallfreddy.exceptions;

public class NoDogPicturesRemainingException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public NoDogPicturesRemainingException() {
    super("All dog pictures have been seen!");
  }
}