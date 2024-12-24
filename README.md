ocr-tess4j-rest
===============

ocr-tess4j-rest - Java Wrapper for Tesseract OCR with Rest API built over Tess4j (http://tess4j.sourceforge.net).

Tess4J is a JNA wrapper for Tesseract OCR API it provides character recognition support for common image formats, 
and multi-page images. The library has been developed and tested on Windows and Linux.


Running ocr-tess4j-rest
-----------------------
`docker-compose build`

`docker-compose up`

ocr-tess4j-rest uses:
------------------

* Spring Boot for Rest
* Spring Boot Data for connecting with mongo db.
* Image + Text (from OCR) is stored in mongo db for `ocr/v1/upload` endpoint.
* Text of uploaded image is displayed back in response if you use `ocr/v1/convert` (Testing or if you want to use something else than mongo).
* Rest Assured is used for testing rest (Tess4jV1). Just remove @Disabled on the Tess4jV1SmokeTest and run the rest test after running `docker-compose up`.
* Logback for logging.
* Gradle for build

** Note: Previous version of ocr-tess4j-rest is at tag 1.1


v1.3 notes:
-----------

Added a new endpoint for ease, you can now convert image to text by doing a GET in browser and passing image url to get text as response.

v1.4 notes:
-----------

Updated boot to 3, tess4j & tessract to 5.x, java 21 

e.g. 

http://localhost:8080/ocr/v1/convert?url=https://raw.githubusercontent.com/arun0009/ocr-tess4j-rest/master/testocr.png&extension=png
