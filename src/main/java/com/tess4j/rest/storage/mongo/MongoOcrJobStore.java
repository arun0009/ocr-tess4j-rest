/* (C) 2026 */
package com.tess4j.rest.storage.mongo;

import com.tess4j.rest.storage.OcrJob;
import com.tess4j.rest.storage.OcrJobStore;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "ocr.storage", name = "type", havingValue = "mongo")
public class MongoOcrJobStore implements OcrJobStore {

  private final MongoOcrJobRepository repository;

  public MongoOcrJobStore(MongoOcrJobRepository repository) {
    this.repository = repository;
  }

  @Override
  public OcrJob save(OcrJob job) {
    return toJob(repository.save(toDocument(job)));
  }

  @Override
  public List<OcrJob> findByUserId(String userId) {
    return repository.findByUserId(userId).stream().map(MongoOcrJobStore::toJob).toList();
  }

  @Override
  public Optional<OcrJob> findById(String id) {
    return repository.findById(id).map(MongoOcrJobStore::toJob);
  }

  private static MongoOcrJobDocument toDocument(OcrJob job) {
    var doc = new MongoOcrJobDocument();
    doc.setId(job.id());
    doc.setUserId(job.userId());
    doc.setImage(job.image());
    doc.setText(job.text());
    doc.setLanguage(job.language());
    doc.setCreatedAt(job.createdAt());
    return doc;
  }

  private static OcrJob toJob(MongoOcrJobDocument doc) {
    return new OcrJob(
        doc.getId(),
        doc.getUserId(),
        doc.getImage(),
        doc.getText(),
        doc.getLanguage(),
        doc.getCreatedAt());
  }
}
