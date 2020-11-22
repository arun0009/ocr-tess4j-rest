# Start with a base image containing Java runtime
FROM openjdk:8-jdk-alpine

RUN apk update && apk add \
    tesseract-ocr \
    ghostscript

# The application's jar file
ARG JAR_FILE=build/libs/ocr-tess4j-rest-1.3.jar

# Add the application's jar to the container
ADD ${JAR_FILE} ocr-tess4j-rest-1.3.jar

# Run the jar file
ENTRYPOINT ["java","-jar","/ocr-tess4j-rest-1.3.jar"]