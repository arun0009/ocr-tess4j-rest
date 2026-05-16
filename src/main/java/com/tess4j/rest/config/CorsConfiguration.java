/* (C) 2026 */
package com.tess4j.rest.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnProperty(prefix = "ocr.cors", name = "enabled", havingValue = "true")
public class CorsConfiguration {

  @Bean
  WebMvcConfigurer ocrCors(OcrProperties properties) {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        var patterns =
            properties.cors().allowedOriginPatterns().isEmpty()
                ? new String[] {"*"}
                : properties.cors().allowedOriginPatterns().toArray(String[]::new);
        registry
            .addMapping("/api/**")
            .allowedOriginPatterns(patterns)
            .allowedMethods("GET", "POST", "OPTIONS")
            .allowedHeaders("*");
      }
    };
  }
}
