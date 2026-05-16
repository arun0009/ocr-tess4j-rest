/* (C) 2026 */
package com.tess4j.rest.ocr;

public class OcrException extends RuntimeException {

  public OcrException(String message) {
    super(message);
  }

  public OcrException(String message, Throwable cause) {
    super(message, cause);
  }
}
