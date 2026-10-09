# syntax=docker/dockerfile:1
# Ubuntu 26.04 packages: Tesseract 5.5.0 + Leptonica 1.86 (matches Tess4J 5.17 / lept4j 1.22).

FROM ubuntu:26.04 AS build
ARG DEBIAN_FRONTEND=noninteractive
RUN apt-get update \
    && apt-get install -y --no-install-recommends openjdk-21-jdk-headless ca-certificates \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /workspace
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
COPY src ./src
RUN chmod +x gradlew && ./gradlew bootJar -x test --no-daemon

FROM ubuntu:26.04
ARG DEBIAN_FRONTEND=noninteractive
ENV LANG=C.UTF-8
ENV TESSDATA_PREFIX=/usr/share/tesseract-ocr/5/tessdata
ENV JNA_LIBRARY_PATH=/usr/local/lib

# Runtime libs are versioned (.so.5 / .so.6). JNA looks up the unversioned names.
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
      openjdk-21-jre-headless \
      curl \
      ca-certificates \
      tesseract-ocr \
      tesseract-ocr-eng \
    && tess="$(dpkg -L libtesseract5 | awk '/libtesseract\.so\.5$/{print; exit}')" \
    && lept="$(dpkg -L libleptonica6 | awk '/libleptonica\.so\.6$/{print; exit}')" \
    && mkdir -p /usr/local/lib \
    && ln -sf "$tess" /usr/local/lib/libtesseract.so \
    && ln -sf "$lept" /usr/local/lib/libleptonica.so \
    && ln -sf "$lept" /usr/local/lib/liblept.so \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /workspace/build/libs/ocr-tess4j-rest-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
