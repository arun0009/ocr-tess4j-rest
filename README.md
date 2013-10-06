ocr-tess4j-rest
============
ocr-tess4j-rest - Java Wrapper for Tesseract OCR with Rest API built over Tess4j (http://tess4j.sourceforge.net).

Tess4J is a JNA wrapper for Tesseract OCR API it provides character recognition support for common image formats, 
and multi-page images. The library has been developed and tested on Windows and Linux.
                
### Installing dependencies

Installing Tesseract <br/>
https://code.google.com/p/tesseract-ocr/ <br/>
https://code.google.com/p/tesseract-ocr/wiki/ReadMe

Install ghostscript (for PDF to Text) <br/>
http://www.ghostscript.com/download/gsdnld.html

<hr/>

ocr-tess4j-rest uses:
------------------

* Spring Boot for Rest
* Spring Data for connecting with mongo db.
* Image + Text (from OCR) is stored in mongo db (image table)
* Rest Assured is used for testing rest (Tess4jV1). Just remove @Ignore on the Tess4jV1SmokeTest and run the rest test.
* Uses mongo db to store image - can we easily removed/changed from Tess4jV1 class if not required.
* Logback for logging.
* Graddle for build/eclipse clean.
* Maven for build/eclipse clean (right now this is builds but for some reason it's not starting embedded tomcat) *BUG*
	

