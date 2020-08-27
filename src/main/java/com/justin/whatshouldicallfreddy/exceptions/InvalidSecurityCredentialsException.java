package com.justin.whatshouldicallfreddy.exceptions;

public class InvalidSecurityCredentialsException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public InvalidSecurityCredentialsException() {
    super("Invalid credentials");
  }
}