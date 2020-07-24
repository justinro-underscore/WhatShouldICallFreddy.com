package com.justin.whatshouldicallfreddy.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import java.util.Objects;

@Entity
public class DogPicture {
  private @Id @GeneratedValue Long id;
  private String fileName;
  
  public DogPicture() {}

  public DogPicture(String fileName) {
    this.fileName = fileName;
  }

  public Long getId() {
    return this.id;
  }

  public String getFileName() {
    return this.fileName;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof DogPicture))
      return false;
    DogPicture dogPicture = (DogPicture) o;
    return Objects.equals(this.id, dogPicture.id) && Objects.equals(this.fileName, dogPicture.fileName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id, this.fileName);
  }

  @Override
  public String toString() {
    return "DogPicture {" + "id=" + this.id + ", fileName='" + this.fileName + "'}";
  }
}