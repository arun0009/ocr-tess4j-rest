# syntax=docker/dockerfile:1
# Leptonica 1.86 + Tesseract 5.5.2 from source (Tess4J 5.18). Runtime: Ubuntu 24.04 + OpenJDK 21.

FROM ubuntu:26.04 AS native-build
ARG DEBIAN_FRONTEND=noninteractive
ARG LEPTONICA_VERSION=1.86.0
ARG TESSERACT_VERSION=5.5.2

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
      build-essential autoconf automake libtool pkg-config wget ca-certificates \
      libpng-dev libjpeg-dev libtiff-dev zlib1g-dev libwebp-dev libopenjp2-7-dev \
      libgif-dev libicu-dev libpango1.0-dev libcairo2-dev \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /tmp

RUN wget -q "https://github.com/DanBloomberg/leptonica/releases/download/${LEPTONICA_VERSION}/leptonica-${LEPTONICA_VERSION}.tar.gz" \
    && tar -xzf "leptonica-${LEPTONICA_VERSION}.tar.gz" \
    && cd "leptonica-${LEPTONICA_VERSION}" \
    && ./configure --prefix=/usr/local \
    && make -j"$(nproc)" \
    && make install \
    && ldconfig \
    && cd .. && rm -rf leptonica-*

RUN wget -q "https://github.com/tesseract-ocr/tesseract/archive/refs/tags/${TESSERACT_VERSION}.tar.gz" -O tesseract.tar.gz \
    && tar -xzf tesseract.tar.gz \
    && cd "tesseract-${TESSERACT_VERSION}" \
    && ./autogen.sh \
    && PKG_CONFIG_PATH=/usr/local/lib/pkgconfig ./configure --prefix=/usr/local \
    && make -j"$(nproc)" \
    && make install \
    && ldconfig \
    && cd .. && rm -rf tesseract*

FROM ubuntu:26.04 AS build
RUN apt-get update \
    && apt-get install -y --no-install-recommends openjdk-21-jdk-headless ca-certificates \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /workspace
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
COPY src ./src
RUN chmod +x gradlew && ./gradlew bootJar -x test --no-daemon

FROM ubuntu:26.04
ENV LANG=C.UTF-8
ENV JNA_LIBRARY_PATH=/usr/local/lib
ENV TESSDATA_PREFIX=/usr/local/share/tessdata

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
      openjdk-21-jre-headless \
      curl ca-certificates \
      libgomp1 \
      libpng16-16t64 \
      libjpeg-turbo8 \
      libtiff6 \
      zlib1g \
      libwebp7 \
      libwebpmux3 \
      libsharpyuv0 \
      libopenjp2-7 \
      libgif7 \
      libcairo2 \
      libpango-1.0-0 \
      libpangocairo-1.0-0 \
      libglib2.0-0t64 \
      libarchive13t64 \
      libicu74 \
    && rm -rf /var/lib/apt/lists/*

COPY --from=native-build /usr/local /usr/local

RUN ldconfig \
    && mkdir -p "${TESSDATA_PREFIX}" \
    && curl -fsSL -o "${TESSDATA_PREFIX}/eng.traineddata" \
      https://github.com/tesseract-ocr/tessdata_best/raw/main/eng.traineddata \
    && ! ldd /usr/local/lib/libtesseract.so | grep -q 'not found' \
    && ! ldd /usr/local/lib/libleptonica.so | grep -q 'not found'

WORKDIR /app
COPY --from=build /workspace/build/libs/ocr-tess4j-rest-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
