/* (C) 2024 */
package com.tess4j.rest.mongo;

import com.tess4j.rest.model.Image;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ImageRepository extends MongoRepository<Image, String> {

  List<Image> findByUserId(String userId);
}
