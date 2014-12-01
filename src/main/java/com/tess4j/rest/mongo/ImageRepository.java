package com.tess4j.rest.mongo;

import com.tess4j.rest.model.Image;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ImageRepository extends MongoRepository<Image, String> {

    public List<Image> findByUserId(String userId);


}
