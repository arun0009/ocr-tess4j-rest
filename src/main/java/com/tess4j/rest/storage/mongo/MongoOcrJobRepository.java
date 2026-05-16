/* (C) 2026 */
package com.tess4j.rest.storage.mongo;

import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.repository.MongoRepository;

@ConditionalOnProperty(prefix = "ocr.storage", name = "type", havingValue = "mongo")
interface MongoOcrJobRepository extends MongoRepository<MongoOcrJobDocument, String> {

  List<MongoOcrJobDocument> findByUserId(String userId);
}
