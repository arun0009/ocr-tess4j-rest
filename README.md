ocr-tess4j-rest
============
ocr-tess4j-rest - Java Wrapper for Tesseract OCR with Rest API built over Tess4j (http://tess4j.sourceforge.net).

Tess4J is a JNA wrapper for Tesseract OCR API it provides character recognition support for common image formats, 
and multi-page images. The library has been developed and tested on Windows and Linux.
                
### Installing dependencies

Installing Tesseract <br/>
https://code.google.com/p/tesseract-ocr/ <br/>
https://code.google.com/p/tesseract-ocr/wiki/ReadMe

Install ghostscript (for PDF to Text) * If you are going to do PDF to text as well*<br/>
http://www.ghostscript.com/download/gsdnld.html

On Mac the easiest way is to use homebrew:
-----------------------------------------

brew install tesseract
brew install gs

<hr/>

ocr-tess4j-rest uses:
------------------

* Spring Boot for Rest
* Spring Data for connecting with mongo db.
* Image + Text (from OCR) is stored in mongo db
* Rest Assured is used for testing rest (Tess4jV1). Just remove @Ignore on the Tess4jV1SmokeTest and run the rest test.
* Uses mongo db to store image.
* Logback for logging.
* Graddle for build/eclipse clean.


** This version wraps tess4j as dependency and pulls it as a dependency jar and has spring boot upgraded to  1.1.9.
   Tested with JDK 1.7.72, Tesseract 3.02.02 on MAC.
