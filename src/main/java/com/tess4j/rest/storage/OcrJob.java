/* (C) 2026 */
package com.tess4j.rest.storage;

import java.time.Instant;

public record OcrJob(
    String id, String userId, byte[] image, String text, String language, Instant createdAt) {}
