/* (C) 2026 */
package com.tess4j.rest.api;

import jakarta.validation.constraints.NotBlank;

public record OcrRequest(@NotBlank String imageBase64, String language) {}
