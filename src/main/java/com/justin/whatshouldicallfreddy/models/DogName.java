package com.justin.whatshouldicallfreddy.models;

import java.util.Comparator;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class DogName {
  private @Id @GeneratedValue Long id;
  private String name;
  private Integer yesVotes;
  private Integer noVotes;

  public static class DogNameSorter implements Comparator<DogName> {
    public int compare(DogName a, DogName b) {
      int numVotes = a.getTotalVotes() - b.getTotalVotes();
      if (numVotes != 0) {
        return numVotes;
      }
      return (int)(a.getId() - b.getId());
    }
  }

  public DogName() {}

  public DogName(String name) {
    this(name, 0, 0);
  }

  public DogName(String name, int yes, int no) {
    this.name = name;
    this.yesVotes = yes;
    this.noVotes = no;
  }

  public void updateDogName(DogName dogName) {
    this.name = dogName.getName() != null ? dogName.getName() : this.name;
    this.yesVotes = dogName.getYesVotes() != null ? dogName.getYesVotes() : this.yesVotes;
    this.noVotes = dogName.getNoVotes() != null ? dogName.getNoVotes() : this.noVotes;
  }
  
  public Long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public Integer getYesVotes() {
    return this.yesVotes;
  }

  public Integer getNoVotes() {
    return this.noVotes;
  }

  public int getTotalVotes() {
    return this.yesVotes + this.noVotes;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setYesVotes(Integer yesVotes) {
    this.yesVotes = yesVotes;
  }

  public void setNoVotes(Integer noVotes) {
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