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

import com.jayway.restassured.response.ResponseBody;
import com.tess4j.rest.model.Image;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;

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
        headers.put("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        Image image = new Image();
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("eurotext.png");
        image.setUserId("arun0009");
        image.setExtension(".png");
        image.setImage(Base64.encodeBase64(IOUtils.toByteArray(inputStream)));
        String response = given().contentType("application/json").headers(headers).when().body(image).expect()
            .statusCode(200).post("http://localhost:8080/ocr/v0.9/upload").asString();
        System.out.println(response);
    }

    @Test
    public void testGetUserImages() throws IOException {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        String response = given().contentType("application/json").headers(headers).when()
            .pathParam("userId", "arun0009").get("http://localhost:8080/ocr/v1/images/users/{userId}")
            .asString();
        System.out.println(response);
    }

}
