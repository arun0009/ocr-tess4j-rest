/* (C) 2026 */
package com.tess4j.rest.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@ConditionalOnProperty(prefix = "ocr.storage", name = "type", havingValue = "mongo")
@EnableMongoRepositories(basePackages = "com.tess4j.rest.storage.mongo")
public class MongoStorageConfiguration {}
