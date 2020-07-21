package com.justin.whatshouldicallfreddy.models;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class DogName {
  private @Id @GeneratedValue Long id;
  private String name;
  private int yesVotes;
  private int noVotes;

  public DogName() {}

  public DogName(String name) {
    this(name, 0, 0);
  }

  public DogName(String name, int yes, int no) {
    this.name = name;
    this.yesVotes = yes;
    this.noVotes = no;
  }
  
  public Long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public int getYesVotes() {
    return this.yesVotes;
  }

  public int getNoVotes() {
    return this.noVotes;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setYesVotes(int yesVotes) {
    this.yesVotes = yesVotes;
  }

  public void setNoVotes(int noVotes) {
    this.noVotes = noVotes;
  }

  public void incYesVotes() {
    this.yesVotes++;
  }

  public void incNoVotes() {
    this.noVotes++;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof DogName))
      return false;
    DogName dogName = (DogName) o;
    return Objects.equals(this.id, dogName.id) && Objects.equals(this.name, dogName.name) &&
      Objects.equals(this.yesVotes, dogName.yesVotes) && Objects.equals(this.noVotes, dogName.noVotes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id, this.name, this.yesVotes, this.noVotes);
  }

  @Override
  public String toString() {
    return "DogName {" + "id=" + this.id + ", name='" + this.name + "', yesVotes=" + this.yesVotes + ", noVotes=" + this.noVotes + "}";
  }
}