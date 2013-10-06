/**
 * Copyright @ 2010 Quan Nguyen
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.sourceforge.tess4j;

import static org.junit.Assert.assertEquals;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;

import net.sourceforge.vietocr.ImageHelper;
import net.sourceforge.vietocr.ImageIOHelper;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.recognition.software.jdeskew.ImageDeskew;

public class TesseractTest {
	static final double MINIMUM_DESKEW_THRESHOLD = 0.05d;

	Tesseract instance;

	public TesseractTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
		instance = Tesseract.getInstance();
	}

	@After
	public void tearDown() {
	}

	/**
	 * Test of doOCR method, of class Tesseract.
	 */
	@Test
	public void testDoOCR_File() throws Exception {
		System.out.println("doOCR on a PNG image");
		URL imageURL = ClassLoader.getSystemResource("eurotext.png");
		String expResult = "The (quick) [brown] {fox} jumps!\nOver the $43,456.78 <lazy> #90 dog";
		String result = instance.doOCR(new File(imageURL.getFile()));
		System.out.println(result);
		assertEquals(expResult, result.substring(0, expResult.length()));
	}

	/**
	 * Test of doOCR method, of class Tesseract.
	 */
	@Test
	public void testDoOCR_File_Rectangle() throws Exception {
		System.out.println("doOCR on a BMP image with bounding rectangle");
		URL imageURL = ClassLoader.getSystemResource("eurotext.bmp");
		Rectangle rect = new Rectangle(0, 0, 1024, 800);
		String expResult = "The (quick) [brown] {fox} jumps!\nOver the $43,456.78 <lazy> #90 dog";
		String result = instance.doOCR(new File(imageURL.getFile()), rect);
		System.out.println(result);
		assertEquals(expResult, result.substring(0, expResult.length()));
	}

	/**
	 * Test of doOCR method, of class Tesseract.
	 */
	public void testDoOCR_List_Rectangle() throws Exception {
		System.out.println("doOCR on a PDF document");
		URL imageURL = ClassLoader.getSystemResource("eurotext.pdf");
		List<IIOImage> imageList = ImageIOHelper.getIIOImageList(new File(imageURL.getFile()));
		String expResult = "The (quick) [brown] {fox} jumps!\nOver the $43,456.78 <lazy> #90 dog";
		String result = instance.doOCR(imageList, null);
		System.out.println(result);
		assertEquals(expResult, result.substring(0, expResult.length()));
	}

	/**
	 * Test of doOCR method, of class Tesseract.
	 */
	@Test
	public void testDoOCR_BufferedImage() throws Exception {
		System.out.println("doOCR on a buffered image of a GIF");
		URL imageURL = ClassLoader.getSystemResource("eurotext.gif");
		BufferedImage bi = ImageIO.read(new File(imageURL.getFile()));
		String expResult = "The (quick) [brown] {fox} jumps!\nOver the $43,456.78 <lazy> #90 dog";
		String result = instance.doOCR(bi);
		System.out.println(result);
		assertEquals(expResult, result.substring(0, expResult.length()));
	}

	/**
	 * Test of deskew algorithm.
	 */
	@Test
	public void testDoOCR_SkewedImage() throws Exception {
		System.out.println("doOCR on a skewed PNG image");
		URL imageURL = ClassLoader.getSystemResource("eurotext_deskew.png");
		BufferedImage bi = ImageIO.read(new File(imageURL.getFile()));
		ImageDeskew id = new ImageDeskew(bi);
		double imageSkewAngle = id.getSkewAngle(); // determine skew angle
		if ((imageSkewAngle > MINIMUM_DESKEW_THRESHOLD || imageSkewAngle < -(MINIMUM_DESKEW_THRESHOLD))) {
			bi = ImageHelper.rotateImage(bi, -imageSkewAngle); // deskew image
		}

		String expResult = "The (quick) [brown] {fox} jumps!\nOver the $43,456.78 <lazy> #90 dog";
		String result = instance.doOCR(bi);
		System.out.println(result);
		assertEquals(expResult, result.substring(0, expResult.length()));
	}
}