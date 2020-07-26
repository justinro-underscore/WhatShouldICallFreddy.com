package com.justin.whatshouldicallfreddy.models;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {
  private @Id Long id = System.nanoTime();
  // private List<DogName> dogNamesSeen;

  public User() {}

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof User))
      return false;
    User user = (User) o;
    return Objects.equals(this.id, user.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  @Override
  public String toString() {
    return "DogName {" + "id=" + this.id + "}";
  }
}