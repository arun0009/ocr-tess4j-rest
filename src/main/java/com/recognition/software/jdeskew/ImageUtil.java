package com.recognition.software.jdeskew;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class ImageUtil {

	public static boolean isBlack(BufferedImage image, int x, int y) {
		if (image.getType() == BufferedImage.TYPE_BYTE_BINARY) {
			WritableRaster raster = image.getRaster();
			int pixelRGBValue = raster.getSample(x, y, 0);
			if (pixelRGBValue == 0) {
				return true;
			}
			else {
				return false;
			}
		}

		int luminanceValue = 140;
		return isBlack(image, x, y, luminanceValue);
	}

	public static boolean isBlack(BufferedImage image, int x, int y, int luminanceCutOff) {
		int pixelRGBValue;
		int r;
		int g;
		int b;
		double luminance = 0.0;

		// return white on areas outside of image boundaries
		if (x < 0 || y < 0 || x > image.getWidth() || y > image.getHeight()) {
			return false;
		}

		try {
			pixelRGBValue = image.getRGB(x, y);
			r = (pixelRGBValue >> 16) & 0xff;
			g = (pixelRGBValue >> 8) & 0xff;
			b = (pixelRGBValue) & 0xff;
			luminance = (r * 0.299) + (g * 0.587) + (b * 0.114);
		}
		catch (Exception e) {
			// ignore.
		}

		return luminance < luminanceCutOff;
	}

}
