<h1 align="center">ocr-tess4j-rest</h1>

<p align="center">
	<strong>Self-hosted image → text OCR over HTTP.</strong><br/>
	URL, file upload, or JSON base64 — one Docker image. Storage is optional: bring your own.
</p>

<p align="center">
	<a href="https://github.com/arun0009/ocr-tess4j-rest/actions/workflows/gradle.yml"><img alt="Build" src="https://github.com/arun0009/ocr-tess4j-rest/actions/workflows/gradle.yml/badge.svg?branch=main"/></a>
	<a href="https://github.com/arun0009/ocr-tess4j-rest/pkgs/container/ocr-tess4j-rest"><img alt="Container" src="https://img.shields.io/badge/GHCR-ocr--tess4j--rest-2496ED?logo=docker&logoColor=white"/></a>
	<a href="LICENSE"><img alt="License" src="https://img.shields.io/github/license/arun0009/ocr-tess4j-rest"/></a>
	<img alt="Java 21" src="https://img.shields.io/badge/Java-21-blue?logo=openjdk&logoColor=white"/>
	<img alt="Spring Boot 4" src="https://img.shields.io/badge/Spring%20Boot-4-6DB33F?logo=springboot&logoColor=white"/>
	<img alt="Tess4J" src="https://img.shields.io/badge/Tess4J-5.18-4CAF50"/>
</p>

<p align="center">
	<a href="#quick-start"><strong>Quick start</strong></a>
	&nbsp;·&nbsp;
	<a href="#api--openapi">API & OpenAPI</a>
	&nbsp;·&nbsp;
	<a href="#bring-your-own-storage">Storage</a>
	&nbsp;·&nbsp;
	<a href="CHANGELOG.md">Changelog</a>
</p>

---

## What is this?

A small **Spring Boot 4** service that wraps [Tesseract 5.5](https://github.com/tesseract-ocr/tesseract) via [Tess4J 5.18](https://github.com/nguyenq/tess4j). Run the container, POST a file or pass an image URL, get `{ "text": "..." }` back.

**OCR is stateless by default.** Persistence is a plug-in — we ship a Mongo reference implementation; you can swap in Postgres, S3 + metadata, or anything else by implementing one interface.

No cloud API keys. No agents. Fits scripts, side projects, and internal tools.

## Quick start

### Clone and run

```bash
git clone https://github.com/arun0009/ocr-tess4j-rest.git
cd ocr-tess4j-rest
```

```bash
make docker-up
```

`make docker-up` copies [`.env.example`](.env.example) → `.env` when missing (URL OCR on by default).

### Try OCR

URL (`OCR_URL_FETCH_ENABLED=true` in `.env`):

```bash
curl -G "http://localhost:8080/api/v1/ocr/url" \
  --data-urlencode "url=https://raw.githubusercontent.com/arun0009/ocr-tess4j-rest/main/testocr.png"
```

File upload:

```bash
curl -F "file=@page.png" http://localhost:8080/api/v1/ocr/file
```

JSON base64:

```bash
curl -H "Content-Type: application/json" \
  -d '{"imageBase64":"..."}' http://localhost:8080/api/v1/ocr
```

### Pull image only (no clone)

```bash
docker run --rm -p 8080:8080 \
  -e OCR_URL_FETCH_ENABLED=true \
  -e OCR_URL_FETCH_ALLOWED_HOSTS=raw.githubusercontent.com \
  ghcr.io/arun0009/ocr-tess4j-rest:latest
```

Production: set `SPRING_PROFILES_ACTIVE=prod` to disable Swagger UI (see `application-prod.yml`).

## API & OpenAPI

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/v1/ocr/url` | OCR from `url` (+ optional `extension`, `language`) |
| `POST` | `/api/v1/ocr/file` | Multipart `file` |
| `POST` | `/api/v1/ocr` | JSON `{ "imageBase64", "language?" }` |
| `POST` | `/api/v1/ocr/jobs` | OCR + persist *(requires storage)* |
| `GET` | `/api/v1/ocr/jobs?userId=` | List jobs *(requires storage)* |

**Response:** `{ "text": "...", "language": "eng" }`

**OpenAPI (springdoc)** — available while the app is running:

| Resource | URL |
|----------|-----|
| Swagger UI | [/swagger-ui.html](http://localhost:8080/swagger-ui.html) |
| OpenAPI 3 JSON | [/v3/api-docs](http://localhost:8080/v3/api-docs) |

```bash
curl -s http://localhost:8080/v3/api-docs | jq .info
```

## Bring your own storage

**Default: no database.** `ocr.storage.type=none` — only OCR endpoints work.

To persist jobs, implement [`OcrJobStore`](src/main/java/com/tess4j/rest/storage/OcrJobStore.java) and register a Spring `@Bean`:

```java
@Bean
OcrJobStore ocrJobStore() {
  return new MyPostgresOcrJobStore(dataSource);
}
```

Or use the **optional Mongo** reference store:

```bash
make docker-mongo   # compose profile: mongo + ocr.storage.type=mongo
```

| `ocr.storage.type` | Behaviour |
|--------------------|-----------|
| `none` *(default)* | OCR only; `/jobs` returns `501` |
| `mongo` | Built-in `MongoOcrJobStore` (+ `spring.profiles.active=mongo`) |
| *custom* | Your `@Bean` implementing `OcrJobStore` |

## Configuration

Copy [`.env.example`](.env.example) → `.env`. Common variables:

| Variable | Default | Purpose |
|----------|---------|---------|
| `OCR_URL_FETCH_ENABLED` | `false` | Enable URL OCR |
| `OCR_URL_FETCH_ALLOWED_HOSTS` | — | Comma-separated host allowlist (SSRF guard) |
| `OCR_LANGUAGE` | `eng` | Tesseract language |
| `OCR_STORAGE_TYPE` | `none` | `none` or `mongo` |
| `OCR_CORS_ENABLED` | `false` | Browser access to `/api/**` |
| `SPRING_PROFILES_ACTIVE` | — | `prod` disables Swagger; `mongo` enables job storage |

URL OCR requires an allowlist, blocks private/reserved IPs, and does not follow HTTP redirects (SSRF hardening).

## Compatibility

| Component | Version |
|-----------|---------|
| Java | 21 (LTS) |
| Spring Boot | 4.0.x |
| Tess4J / Tesseract | 5.18 / 5.5 |
| Docker | Ubuntu 24.04, OpenJDK 21, `tessdata_best` (eng) |

## Makefile

| Command | Action |
|---------|--------|
| `make docker-up` | Build & run OCR (`.env`) |
| `make docker-mongo` | OCR + Mongo storage |
| `make build` | `./gradlew build` |
| `make test` | Unit tests |
| `make run` | Local `bootRun` *(needs Tesseract)* |

## Status

| | |
|---|---|
| **Release** | **2.0.0** on branch `main` (full rewrite). Branch `master` keeps 1.x for now. |
| **Image** | [`ghcr.io/arun0009/ocr-tess4j-rest`](https://github.com/arun0009/ocr-tess4j-rest/pkgs/container/ocr-tess4j-rest) |
| **CI** | Gradle build, CodeQL, Docker publish on `main` / tags |
| **Scope** | Printed/scan OCR via Tesseract — not handwriting or layout AI |

See [CHANGELOG.md](CHANGELOG.md) for release notes.

## License

[LICENSE](LICENSE)
