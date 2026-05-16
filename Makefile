.DEFAULT_GOAL := help

.PHONY: help build test run clean setup docker-build docker-up docker-mongo spotless

GRADLE := ./gradlew

help:
	@echo "ocr-tess4j-rest — common targets:"
	@echo ""
	@echo "  make docker-up     Run OCR in Docker (creates .env from .env.example if needed)"
	@echo "  make docker-mongo  OCR + Mongo storage profile"
	@echo "  make docker-build  Build Docker image only"
	@echo "  make build         ./gradlew build"
	@echo "  make test          ./gradlew test"
	@echo "  make run           ./gradlew bootRun  (needs Tesseract on host)"
	@echo "  make spotless      Format sources"
	@echo "  make clean         ./gradlew clean"
	@echo "  make setup         Copy .env.example -> .env if missing"

setup:
	@test -f .env || cp .env.example .env

build:
	$(GRADLE) build

test:
	$(GRADLE) test

run:
	$(GRADLE) bootRun

clean:
	$(GRADLE) clean

spotless:
	$(GRADLE) spotlessApply

docker-build:
	docker compose build

docker-up: setup
	docker compose up --build

docker-mongo: setup
	docker compose --profile mongo up --build
