/* (C) 2026 */
package com.tess4j.rest.config;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TesseractConfiguration {

  @Bean
  Tesseract tesseract(OcrProperties properties) {
    var tesseract = new Tesseract();
    if (properties.dataPath() != null && !properties.dataPath().isBlank()) {
      tesseract.setDatapath(properties.dataPath());
    }
    tesseract.setLanguage(properties.language());
    tesseract.setPageSegMode(properties.pageSegMode());
    tesseract.setOcrEngineMode(properties.ocrEngineMode());
    return tesseract;
  }
}
