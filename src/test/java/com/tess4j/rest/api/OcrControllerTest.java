/* (C) 2026 */
package com.tess4j.rest.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tess4j.rest.ocr.OcrService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OcrController.class)
class OcrControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private OcrService ocr;

  @Test
  void jsonOcr() throws Exception {
    when(ocr.resolveLanguage(null)).thenReturn("eng");
    when(ocr.recognize(any(byte[].class), eq("eng"))).thenReturn("hello");

    mockMvc
        .perform(
            post("/api/v1/ocr")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"imageBase64\":\"aGVsbG8=\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.text").value("hello"));
  }

  @Test
  void urlOcr() throws Exception {
    when(ocr.resolveLanguage(null)).thenReturn("eng");
    when(ocr.recognizeFromUrl("https://example.com/a.png", "png", "eng")).thenReturn("text");

    mockMvc
        .perform(get("/api/v1/ocr/url").param("url", "https://example.com/a.png"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.text").value("text"));
  }

  @Test
  void jobsRequireStore() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/ocr/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":\"u1\",\"imageBase64\":\"aGk=\"}"))
        .andExpect(status().isNotImplemented());
  }
}
