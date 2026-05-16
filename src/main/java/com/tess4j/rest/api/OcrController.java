/* (C) 2026 */
package com.tess4j.rest.api;

import com.tess4j.rest.ocr.OcrService;
import com.tess4j.rest.storage.OcrJob;
import com.tess4j.rest.storage.OcrJobStore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/ocr")
@Tag(name = "OCR")
public class OcrController {

  private final OcrService ocr;
  private final OcrJobStore jobStore;

  public OcrController(OcrService ocr, ObjectProvider<OcrJobStore> stores) {
    this.ocr = ocr;
    this.jobStore = stores.getIfAvailable();
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Recognize text from a base64-encoded image")
  public OcrResponse recognizeJson(@Valid @RequestBody OcrRequest request) {
    var language = ocr.resolveLanguage(request.language());
    var text = ocr.recognize(Base64.getDecoder().decode(request.imageBase64()), language);
    return new OcrResponse(text, language);
  }

  @PostMapping(
      path = "/file",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Recognize text from an uploaded file")
  public OcrResponse recognizeFile(
      @RequestPart("file") MultipartFile file, @RequestParam(required = false) String language)
      throws Exception {
    var resolved = ocr.resolveLanguage(language);
    var text = ocr.recognize(file.getBytes(), resolved);
    return new OcrResponse(text, resolved);
  }

  @GetMapping(path = "/url", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Recognize text from an image URL (allowlist required)")
  public OcrResponse recognizeUrl(
      @RequestParam String url,
      @RequestParam(defaultValue = "png") String extension,
      @RequestParam(required = false) String language) {
    var resolved = ocr.resolveLanguage(language);
    var text = ocr.recognizeFromUrl(url, extension, resolved);
    return new OcrResponse(text, resolved);
  }

  @PostMapping(
      path = "/jobs",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Run OCR and persist the job (requires an OcrJobStore bean)")
  public OcrJob storeJob(@Valid @RequestBody StoreJobRequest request) {
    var language = ocr.resolveLanguage(request.language());
    var image = Base64.getDecoder().decode(request.imageBase64());
    var text = ocr.recognize(image, language);
    return requireStore()
        .save(new OcrJob(null, request.userId(), image, text, language, Instant.now()));
  }

  @GetMapping(path = "/jobs", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "List stored jobs for a user")
  public List<OcrJob> listJobs(@RequestParam String userId) {
    return requireStore().findByUserId(userId);
  }

  private OcrJobStore requireStore() {
    if (jobStore == null) {
      throw new ResponseStatusException(
          HttpStatus.NOT_IMPLEMENTED,
          "No OcrJobStore configured. Set ocr.storage.type=mongo or provide your own @Bean.");
    }
    return jobStore;
  }
}
