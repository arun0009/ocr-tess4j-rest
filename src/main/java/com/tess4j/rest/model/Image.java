/* (C) 2024 */
package com.tess4j.rest.model;

import org.springframework.data.annotation.Id;

public class Image {

  @Id private String id;

  private String userId;

  private byte[] image;

  private String extension;

  private String text;

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public byte[] getImage() {
    return image;
  }

  public void setImage(byte[] image) {
    this.image = image;
  }

  public String getExtension() {
    return extension;
  }

  public void setExtension(String extension) {
    this.extension = extension;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return String.format(
        "Image[userId=%s, image='%s', extenstion='%s', text='%s']", userId, image, extension, text);
  }
}
