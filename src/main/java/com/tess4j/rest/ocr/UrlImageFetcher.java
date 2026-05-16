/* (C) 2026 */
package com.tess4j.rest.ocr;

import com.tess4j.rest.config.OcrProperties;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URI;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class UrlImageFetcher {

  private final OcrProperties properties;

  public UrlImageFetcher(OcrProperties properties) {
    this.properties = properties;
  }

  public Path download(String url, String extension) throws IOException {
    var urlFetch = properties.urlFetch();
    if (!urlFetch.enabled()) {
      throw new OcrException("URL OCR is disabled. Set ocr.url-fetch.enabled=true.");
    }
    if (urlFetch.allowedHosts() == null || urlFetch.allowedHosts().isEmpty()) {
      throw new OcrException(
          "URL OCR has no allowed hosts. Set ocr.url-fetch.allowed-hosts (comma-separated).");
    }

    var uri = URI.create(url);
    validate(uri);

    var suffix = extension.startsWith(".") ? extension : "." + extension;
    var tempFile = Files.createTempFile("ocr_url_", suffix);
    URLConnection connection = uri.toURL().openConnection();
    if (connection instanceof HttpURLConnection http) {
      http.setInstanceFollowRedirects(false);
    }
    connection.setConnectTimeout((int) urlFetch.connectTimeout().toMillis());
    connection.setReadTimeout((int) urlFetch.readTimeout().toMillis());
    connection.setRequestProperty("User-Agent", "ocr-tess4j-rest/2.0");

    try (InputStream input = connection.getInputStream()) {
      Files.copy(input, tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException ex) {
      Files.deleteIfExists(tempFile);
      throw ex;
    }
    return tempFile;
  }

  private void validate(URI uri) throws IOException {
    var scheme = uri.getScheme();
    if (scheme == null || (!scheme.equalsIgnoreCase("https") && !scheme.equalsIgnoreCase("http"))) {
      throw new OcrException("Only http(s) URLs are supported.");
    }

    var host = uri.getHost();
    if (host == null || host.isBlank()) {
      throw new OcrException("URL must include a host.");
    }
    var normalizedHost = host.toLowerCase(Locale.ROOT);

    if (properties.urlFetch().allowedHosts().stream()
        .noneMatch(allowed -> hostMatches(normalizedHost, allowed))) {
      throw new OcrException("Host not allowed: " + normalizedHost);
    }

    for (var address : InetAddress.getAllByName(normalizedHost)) {
      if (address.isAnyLocalAddress()
          || address.isLoopbackAddress()
          || address.isLinkLocalAddress()
          || address.isSiteLocalAddress()) {
        throw new OcrException("URL resolves to a private address.");
      }
    }
  }

  private static boolean hostMatches(String host, String allowed) {
    var normalized = allowed.toLowerCase(Locale.ROOT);
    return host.equals(normalized) || host.endsWith("." + normalized);
  }
}
