/* (C) 2026 */
package com.tess4j.rest.ocr;

import com.tess4j.rest.config.OcrProperties;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class OcrService {

  private static final Logger log = LoggerFactory.getLogger(OcrService.class);

  private final Tesseract tesseract;
  private final OcrProperties properties;
  private final UrlImageFetcher urlImageFetcher;

  public OcrService(
      Tesseract tesseract, OcrProperties properties, UrlImageFetcher urlImageFetcher) {
    this.tesseract = tesseract;
    this.properties = properties;
    this.urlImageFetcher = urlImageFetcher;
  }

  public String recognize(byte[] imageBytes, String language) {
    checkSize(imageBytes.length);
    Path temp = null;
    try {
      temp = Files.createTempFile("ocr_", ".img");
      Files.write(temp, imageBytes);
      return recognize(temp, language);
    } catch (IOException ex) {
      throw new OcrException("Failed to read image.", ex);
    } finally {
      delete(temp);
    }
  }

  public String recognize(Path file, String language) {
    try {
      return withLanguage(
          language,
          () -> {
            BufferedImage image = ImageIO.read(file.toFile());
            if (image != null) {
              return runOcr(image);
            }
            return runOcr(file.toFile());
          });
    } catch (IOException | TesseractException ex) {
      throw new OcrException("OCR failed.", ex);
    }
  }

  public String recognizeFromUrl(String url, String extension, String language) {
    Path temp = null;
    try {
      temp = urlImageFetcher.download(url, extension);
      checkSize(Files.size(temp));
      return recognize(temp, language);
    } catch (IOException ex) {
      throw new OcrException("Failed to fetch image.", ex);
    } finally {
      delete(temp);
    }
  }

  public String defaultLanguage() {
    return properties.language();
  }

  public String resolveLanguage(String requested) {
    return StringUtils.hasText(requested) ? requested.trim() : properties.language();
  }

  private String withLanguage(String language, OcrAction action)
      throws TesseractException, IOException {
    synchronized (tesseract) {
      var active = resolveLanguage(language);
      tesseract.setLanguage(active);
      return action.run();
    }
  }

  private String runOcr(BufferedImage image) throws TesseractException {
    var text = tesseract.doOCR(image);
    log.debug("OCR produced {} characters", text == null ? 0 : text.length());
    return normalize(text);
  }

  private String runOcr(java.io.File file) throws TesseractException {
    return normalize(tesseract.doOCR(file));
  }

  private static String normalize(String text) {
    return text == null ? "" : text.trim();
  }

  private void checkSize(long bytes) {
    if (bytes > properties.maxUploadBytes()) {
      throw new OcrException(
          "Image exceeds max size of " + properties.maxUploadBytes() + " bytes.");
    }
  }

  private static void delete(Path path) {
    if (path != null) {
      try {
        Files.deleteIfExists(path);
      } catch (IOException ex) {
        log.warn("Could not delete {}", path, ex);
      }
    }
  }

  @FunctionalInterface
  private interface OcrAction {
    String run() throws TesseractException, IOException;
  }
}
