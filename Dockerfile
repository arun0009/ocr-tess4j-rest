FROM ubuntu:22.04

ARG DEBIAN_FRONTEND=noninteractive
ENV LANG=C.UTF-8

# Install system dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential autoconf automake libtool pkg-config \
    wget curl git ca-certificates gnupg \
    libjpeg-dev libpng-dev libtiff-dev zlib1g-dev \
    libopenjp2-7-dev libwebp-dev libgif-dev \
    libicu-dev libpango1.0-dev libcairo2-dev \
    asciidoc xsltproc docbook-xsl \
    && rm -rf /var/lib/apt/lists/*

# Install Amazon Corretto Java 24
RUN wget -O- https://apt.corretto.aws/corretto.key | gpg --dearmor -o /usr/share/keyrings/corretto-keyring.gpg && \
    echo "deb [signed-by=/usr/share/keyrings/corretto-keyring.gpg] https://apt.corretto.aws stable main" > /etc/apt/sources.list.d/corretto.list && \
    apt-get update && apt-get install -y java-24-amazon-corretto-jdk && \
    rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME=/usr/lib/jvm/java-24-amazon-corretto
ENV PATH="$JAVA_HOME/bin:$PATH"

WORKDIR /tmp

# Build and install Leptonica 1.85.0
ARG LEPTONICA_VERSION=1.85.0
RUN wget -q https://github.com/DanBloomberg/leptonica/releases/download/${LEPTONICA_VERSION}/leptonica-${LEPTONICA_VERSION}.tar.gz && \
    tar -xzf leptonica-${LEPTONICA_VERSION}.tar.gz && \
    cd leptonica-${LEPTONICA_VERSION} && \
    ./configure && make -j"$(nproc)" && make install && ldconfig && \
    cd .. && rm -rf leptonica*

# Build and install Tesseract 5.5.1
ARG TESSERACT_VERSION=5.5.1
RUN wget -q https://github.com/tesseract-ocr/tesseract/archive/refs/tags/${TESSERACT_VERSION}.tar.gz -O tesseract.tar.gz && \
    tar -xzf tesseract.tar.gz && \
    cd tesseract-${TESSERACT_VERSION} && \
    ./autogen.sh && ./configure && make -j"$(nproc)" && make install && ldconfig && \
    cd .. && rm -rf tesseract*

# Download English traineddata
ENV TESSDATA_PREFIX=/usr/local/share/tessdata
RUN mkdir -p $TESSDATA_PREFIX && \
    wget -qO $TESSDATA_PREFIX/eng.traineddata https://github.com/tesseract-ocr/tessdata_best/raw/main/eng.traineddata

# Copy application JAR
WORKDIR /app
COPY build/libs/ocr-tess4j-rest-1.6.jar app.jar

# Run Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]