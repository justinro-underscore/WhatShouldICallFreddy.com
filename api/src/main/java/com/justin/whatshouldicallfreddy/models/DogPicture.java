package com.justin.whatshouldicallfreddy.models;

import javax.imageio.ImageIO;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Entity
public class DogPicture {
  private @Id @GeneratedValue Long id;
  private String fileName;
  private int normalizedHeight;
  private int normalizedCenterX;
  private int normalizedCenterY;

  final private double MAX_WIDTH_PX = 900; // This is hardcoded, discovered from the front end
  
  public DogPicture() {}

  public DogPicture(String fileName, int centerX, int centerY) {
    try {
      this.fileName = fileName;
      BufferedImage img = ImageIO.read(new File("api/src/main/resources/" + this.fileName));
      int width = img.getWidth();
      int height = img.getHeight();

      double factor = MAX_WIDTH_PX / width;
      this.normalizedHeight = (int)Math.floor(factor * height);
      this.normalizedCenterX = (int)Math.floor(factor * centerX);
      this.normalizedCenterY = (int)Math.floor(factor * centerY);
    }
    catch (IOException e) {}
  }

  public Long getId() {
    return this.id;
  }

  public String getFileName() {
    return this.fileName;
  }

  public int getNormalizedHeight() {
    return this.normalizedHeight;
  }

  public int getNormalizeCenterX() {
    return this.normalizedCenterX;
  }

  public int getNormalizeCenterY() {
    return this.normalizedCenterY;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public void setNormalizedHeight(int normalizedHeight) {
    this.normalizedHeight = normalizedHeight;
  }

  public void setNormalizedCenterX(int normalizedCenterX) {
    this.normalizedCenterX = normalizedCenterX;
  }

  public void setNormalizedCenterY(int normalizedCenterY) {
    this.normalizedCenterY = normalizedCenterY;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof DogPicture))
      return false;
    DogPicture dogPicture = (DogPicture) o;
    return Objects.equals(this.id, dogPicture.id) && Objects.equals(this.fileName, dogPicture.fileName) && Objects.equals(this.normalizedHeight, dogPicture.normalizedHeight)
      && Objects.equals(this.normalizedCenterX, dogPicture.normalizedCenterX) && Objects.equals(this.normalizedCenterY, dogPicture.normalizedCenterY);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id, this.fileName, this.normalizedHeight, this.normalizedCenterX, this.normalizedCenterY);
  }

  @Override
  public String toString() {
    return "DogPicture {" + "id=" + this.id + ", fileName='" + this.fileName + "', normalizedHeight=" + this.normalizedHeight +
      ", normalizedCenterX: " + this.normalizedCenterX + ", normalizedCenterY: " + this.normalizedCenterY + "}";
  }
}