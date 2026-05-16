# Changelog

## Unreleased

### Security

- URL OCR: disable HTTP redirects to prevent allowlist bypass

### Changed

- GHCR images published for `linux/amd64` and `linux/arm64`
- `SPRING_PROFILES_ACTIVE=prod` disables Swagger UI / OpenAPI docs

## 2.0.0

**Full rewrite.** No compatibility with pre-2.x APIs or Docker layout.

### Added

- REST API under `/api/v1/ocr` (URL, file, JSON base64)
- OpenAPI 3 via springdoc (`/v3/api-docs`, `/swagger-ui.html`)
- Pluggable `OcrJobStore` with optional Mongo reference implementation
- Docker image on GHCR, multi-stage build, `tessdata_best`
- URL OCR with host allowlist and private-IP blocking
- Spring Boot 4, Java 21, Makefile, `.env.example`

### Removed

- Legacy `/ocr/v1/*` endpoints
- Mandatory MongoDB for basic OCR
