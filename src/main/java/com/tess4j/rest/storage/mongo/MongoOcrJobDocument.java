/* (C) 2026 */
package com.tess4j.rest.storage.mongo;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ocr_jobs")
class MongoOcrJobDocument {

  @Id private String id;
  private String userId;
  private byte[] image;
  private String text;
  private String language;
  private Instant createdAt;

  String getId() {
    return id;
  }

  void setId(String id) {
    this.id = id;
  }

  String getUserId() {
    return userId;
  }

  void setUserId(String userId) {
    this.userId = userId;
  }

  byte[] getImage() {
    return image;
  }

  void setImage(byte[] image) {
    this.image = image;
  }

  String getText() {
    return text;
  }

  void setText(String text) {
    this.text = text;
  }

  String getLanguage() {
    return language;
  }

  void setLanguage(String language) {
    this.language = language;
  }

  Instant getCreatedAt() {
    return createdAt;
  }

  void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
