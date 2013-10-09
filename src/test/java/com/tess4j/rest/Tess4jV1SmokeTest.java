/**
 * Copyright @ 2013 Arun Gopalpuri
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

import static com.jayway.restassured.RestAssured.given;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import com.jayway.restassured.response.ResponseBody;
import com.tess4j.model.Image;

@Ignore
public class Tess4jV1SmokeTest {

	@Test
	public void testHealth() {
		ResponseBody body = given().when().get("http://localhost:8080/ocr/ping");
		System.out.println(body.asString());
	}

	@Test
	public void testDoOcr() throws IOException {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Accept", MediaType.APPLICATION_JSON);
		headers.put("Content-Type", MediaType.APPLICATION_JSON);

		Image image = new Image();
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("eurotext.gif");
		image.setUserId("arun0009");
		image.setImage(Base64.encodeBase64String(IOUtils.toString(inputStream).getBytes()));
		String response = given().contentType("application/json").headers(headers).when().body(image).expect()
				.statusCode(200).post("http://localhost:8080/ocr/v1/upload").asString();
		System.out.println(response);
	}

	@Test
	public void testGetUserImage() throws IOException {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Accept", MediaType.APPLICATION_JSON);
		headers.put("Content-Type", MediaType.APPLICATION_JSON);

		String response = given().contentType("application/json").headers(headers).when()
				.pathParam("user", "arun0009").pathParam("searchText", "brown").get("http://localhost:8080/ocr/v1/image/{user}/{searchText}")
				.asString();
		System.out.println(response);
	}

}
