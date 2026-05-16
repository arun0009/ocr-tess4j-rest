/* (C) 2026 */
package com.tess4j.rest.storage;

import java.util.List;
import java.util.Optional;

/**
 * Bring-your-own storage for OCR jobs.
 *
 * <p>The service runs without any {@code OcrJobStore} bean ({@code ocr.storage.type=none}). To
 * persist results, provide your own implementation as a Spring {@code @Bean}, or enable the
 * reference Mongo store ({@code ocr.storage.type=mongo}, profile {@code mongo}).
 */
public interface OcrJobStore {

  OcrJob save(OcrJob job);

  List<OcrJob> findByUserId(String userId);

  Optional<OcrJob> findById(String id);
}
