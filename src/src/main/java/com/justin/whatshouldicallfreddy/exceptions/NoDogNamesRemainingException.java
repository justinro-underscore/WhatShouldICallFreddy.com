package com.justin.whatshouldicallfreddy.exceptions;

public class NoDogNamesRemainingException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public NoDogNamesRemainingException() {
    super("All dog names have been voted on!");
  }
}