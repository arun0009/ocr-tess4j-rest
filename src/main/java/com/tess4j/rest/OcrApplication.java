/* (C) 2026 */
package com.tess4j.rest;

import com.tess4j.rest.config.OcrProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(OcrProperties.class)
public class OcrApplication {

  public static void main(String[] args) {
    SpringApplication.run(OcrApplication.class, args);
  }
}
