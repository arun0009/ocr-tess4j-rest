# Start with a base image containing Java runtime
FROM amazoncorretto:22.0.0-alpine3.19

ENV TESSDATA_PREFIX=/usr/share/tesseract-ocr/5/tessdata/

RUN apk update && \
    apk add tesseract-ocr tesseract-ocr-data-eng ghostscript

ENV TESSDATA_PREFIX=/usr/share/tessdata

# The application's jar file
ARG JAR_FILE=build/libs/ocr-tess4j-rest-1.4.jar

# Add the application's jar to the container
ADD ${JAR_FILE} ocr-tess4j-rest-1.4.jar

# Run the jar file
ENTRYPOINT ["java","-jar","/ocr-tess4j-rest-1.4.jar"]