/**
 * Copyright @ 2010 Arun Gopalpuri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tess4j.rest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.ws.rs.core.MediaType;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.lowagie.text.pdf.codec.Base64;
import com.tess4j.model.Image;
import com.tess4j.model.Status;
import com.tess4j.mongo.MongoConfiguration;

@RestController
@EnableAutoConfiguration
@ComponentScan("com.tess4j.*")
public class Tess4jV1 {

	private Logger LOGGER = LoggerFactory.getLogger(Tess4jV1.class);

	@RequestMapping(value = "ocr/ping", method = RequestMethod.GET)
	public Status ping() throws Exception {
		return new Status("OK");
	}

	@RequestMapping(value = "ocr/v1/upload", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public Status doOcrFile(@RequestBody
	final Image image) throws Exception {
		File tmpFile = File.createTempFile("ocr_image", image.getExtension());
		try {
			FileUtils.writeByteArrayToFile(tmpFile, Base64.decode(image.getImage()));
			Tesseract tesseract = Tesseract.getInstance(); // JNA Interface Mapping
			String imageText = tesseract.doOCR(tmpFile);
			LOGGER.info("OCR Image Text = " + imageText);
		}
		catch (Exception e) {
			LOGGER.error("Exception while converting/uploading image: ", e);
			throw new TesseractException();
		}
		finally {
			tmpFile.delete();
		}
		return new Status("success");
	}

	@RequestMapping(value = "ocr/v2/upload", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public Status doOcr(@RequestBody
	final Image image) throws IOException, TesseractException {
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decode(image.getImage()));
			Tesseract tesseract = Tesseract.getInstance(); // JNA Interface Mapping
			String imageText = tesseract.doOCR(ImageIO.read(bis));
			image.setText(imageText);
			ApplicationContext context = new AnnotationConfigApplicationContext(MongoConfiguration.class);
			MongoOperations mongoOperations = (MongoOperations) context.getBean("mongoTemplate");
			mongoOperations.save(image);
			((AbstractApplicationContext) context).close();
			LOGGER.info("OCR Result = " + imageText);
		}
		catch (Exception e) {
			LOGGER.error("TessearctException while converting/uploading image: ", e);
			throw new TesseractException();
		}
		return new Status("success");
	}

	public static void main(String[] args) {
		SpringApplication.run(Tess4jV1.class, args);
	}
}
