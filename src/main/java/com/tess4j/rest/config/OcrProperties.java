/* (C) 2026 */
package com.tess4j.rest.config;

import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "ocr")
public record OcrProperties(
    @DefaultValue("eng") String language,
    @DefaultValue("") String dataPath,
    @DefaultValue("3") int pageSegMode,
    @DefaultValue("1") int ocrEngineMode,
    @DefaultValue("10485760") long maxUploadBytes,
    UrlFetch urlFetch,
    Storage storage,
    Cors cors) {

  public record Cors(
      @DefaultValue("false") boolean enabled, @DefaultValue List<String> allowedOriginPatterns) {}

  public record UrlFetch(
      @DefaultValue("false") boolean enabled,
      @DefaultValue List<String> allowedHosts,
      @DefaultValue("10s") Duration connectTimeout,
      @DefaultValue("30s") Duration readTimeout) {}

  public record Storage(@DefaultValue("none") String type) {
    public boolean isMongo() {
      return "mongo".equalsIgnoreCase(type);
    }
  }
}
