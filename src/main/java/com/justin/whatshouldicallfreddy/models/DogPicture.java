package com.justin.whatshouldicallfreddy.models;

import javax.imageio.ImageIO;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

@Entity
public class DogPicture {
  private @Id @GeneratedValue Long id;
  private String fileName;
  private int normalizedWidth;
  private int normalizedHeight;
  private int normalizedCenterX;
  private int normalizedCenterY;

  final private double NORMALIZED_WIDTH_PX = 1000; // Aribitrary normalized width
  
  public DogPicture() {}

  public DogPicture(String fileName, int centerX, int centerY) {
    try {
      setNewDogPicture(fileName, centerX, centerY);
    }
    catch (IOException e) {}
  }

  public DogPicture(int centerX, int centerY) {
    this.fileName = null;
    this.normalizedCenterX = centerX;
    this.normalizedCenterY = centerY;
    this.normalizedHeight = 0;
    this.normalizedWidth = 0;
  }

  /**
   * This assumes normalizedCenterX and normalizedCenterY contain the non-normalized values of centerX and centerY
   */
  public void setNewDogPicture(String fileName) throws IOException {
    if (this.fileName == null) {
      setNewDogPicture(fileName, this.normalizedCenterX, this.normalizedCenterY);
    }
  }

  private void setNewDogPicture(String fileName, int centerX, int centerY) throws IOException {
    BufferedImage img = ImageIO.read(getClass().getResourceAsStream(fileName));
    int width = img.getWidth();
    int height = img.getHeight();

    setNormalizedValues(width, height, centerX, centerY);

    this.fileName = fileName;
  }

  public void setNormalizedValues(int width, int height, int centerX, int centerY) {
    double factor = NORMALIZED_WIDTH_PX / width;
    this.normalizedWidth = (int)NORMALIZED_WIDTH_PX;
    this.normalizedHeight = (int)Math.floor(factor * height);
    this.normalizedCenterX = (int)Math.floor(factor * centerX);
    this.normalizedCenterY = (int)Math.floor(factor * centerY);
  }

  public Long getId() {
    return this.id;
  }

  public String getFileName() {
    return this.fileName;
  }

  public int getNormalizedWidth() {
    return this.normalizedWidth;
  }

  public int getNormalizedHeight() {
    return this.normalizedHeight;
  }

  public int getNormalizedCenterX() {
    return this.normalizedCenterX;
  }

  public int getNormalizedCenterY() {
    return this.normalizedCenterY;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public void setNormalizedWidth(int normalizedWidth) {
    this.normalizedWidth = normalizedWidth;
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
    return Objects.equals(this.id, dogPicture.id) && Objects.equals(this.fileName, dogPicture.fileName) 
      && Objects.equals(this.normalizedWidth, dogPicture.normalizedWidth) && Objects.equals(this.normalizedHeight, dogPicture.normalizedHeight)
      && Objects.equals(this.normalizedCenterX, dogPicture.normalizedCenterX) && Objects.equals(this.normalizedCenterY, dogPicture.normalizedCenterY);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id, this.fileName, this.normalizedWidth, this.normalizedHeight, this.normalizedCenterX, this.normalizedCenterY);
  }

  @Override
  public String toString() {
    return "DogPicture {" + "id=" + this.id + ", fileName='" + this.fileName + "', normalizedWidth=" + this.normalizedWidth + ", normalizedHeight=" + this.normalizedHeight +
      ", normalizedCenterX: " + this.normalizedCenterX + ", normalizedCenterY: " + this.normalizedCenterY + "}";
  }
}