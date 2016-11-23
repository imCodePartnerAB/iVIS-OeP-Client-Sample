/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.image;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

import se.unlogic.standardutils.io.FileUtils;

public class ImageUtils {

	//TODO this class needs a makeover and a rethink to bring all loose ends together

	public static final String JPG = "jpg";
	public static final String JPEG = "jpeg";
	public static final String GIF = "gif";
	public static final String PNG = "png";
	public static final String BMP = "bmp";
	public static final String WBMP = "wbmp";

	public static BufferedImage getImageByResource(String url) throws IOException {

		return ImageIO.read(ImageUtils.class.getResource(url));
	}

	public static BufferedImage getImageByURL(URL resource) throws IOException {

		return ImageIO.read(resource);
	}

	public static BufferedImage getImage(String path) throws IOException {

		return ImageIO.read(new File(path));
	}

	public static BufferedImage getImage(byte[] data) throws IOException {

		return ImageIO.read(new ByteArrayInputStream(data));
	}

	public static BufferedImage getImage(File file) throws IOException {

		return ImageIO.read(file);
	}

	public static BufferedImage getImage(InputStream inputStream) throws IOException {

		return ImageIO.read(inputStream);
	}

	public static BufferedImage scaleImage(BufferedImage image, int maxHeight, int maxWidth, int quality, int imageType) {

		int original_width = image.getWidth();
		int original_height = image.getHeight();

		int new_width = original_width;
		int new_height = original_height;

		// first check if we need to scale width
		if(original_width > maxWidth){
			//scale width to fit
			new_width = maxWidth;
			//scale height to maintain aspect ratio
			new_height = (new_width * original_height) / original_width;
		}

		// then check if we need to scale even with the new height
		if(new_height > maxHeight){
			//scale height to fit instead
			new_height = maxHeight;
			//scale width to maintain aspect ratio
			new_width = (new_height * original_width) / original_height;
		}

		return scale(image, new_height, new_width, quality, imageType);
	}

	public static BufferedImage scaleImageByWidth(BufferedImage image, int maxWidth, int quality, int imageType) {

		int original_width = image.getWidth();
		int original_height = image.getHeight();

		int new_width = original_width;
		int new_height = original_height;

		// first check if we need to scale width
		if(original_width > maxWidth){
			//scale width to fit
			new_width = maxWidth;
			//scale height to maintain aspect ratio
			new_height = (new_width * original_height) / original_width;
		}

		return scale(image, new_height, new_width, quality, imageType);
	}

	public static BufferedImage scaleImageByHeight(BufferedImage image, int maxHeight, int quality, int imageType) {

		int original_width = image.getWidth();
		int original_height = image.getHeight();

		int new_width = original_width;
		int new_height = original_height;

		// then check if we need to scale even with the new height
		if(new_height > maxHeight){
			//scale height to fit instead
			new_height = maxHeight;
			//scale width to maintain aspect ratio
			new_width = (new_height * original_width) / original_height;
		}

		return scale(image, new_height, new_width, quality, imageType);
	}

	public static byte[] convertImage(BufferedImage image, String format) throws IOException, NullPointerException {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		ImageIO.write(image, format, outputStream);

		return outputStream.toByteArray();
	}

	public static void writeImage(BufferedImage image, String path, String format) throws IOException, NullPointerException {

		// write image to file
		if(!path.endsWith("." + format)){
			path += "." + format;
		}

		writeImage(image, new File(path), format);
	}

	public static void writeImage(BufferedImage image, File file, String format) throws IOException, NullPointerException {

		ImageIO.write(image, format, file);
	}

	public static BufferedImage scale(BufferedImage image, double xFactor, double yFactor, int imageType) {

		// scale image based on factor x and y
		AffineTransform scaleTransform = new AffineTransform();
		scaleTransform.scale(xFactor, yFactor);

		BufferedImage result = new BufferedImage((int)(image.getWidth() * xFactor), (int)(image.getHeight() * yFactor), imageType);

		Graphics2D g2 = (Graphics2D)result.getGraphics();

		setBackground(g2, result, image);

		g2.drawImage(image, scaleTransform, null);

		return result;
	}

	public static BufferedImage scale(BufferedImage image, int height, int width, int quality, int imageType) {

		BufferedImage result = new BufferedImage(width, height, imageType);

		Graphics2D g2 = (Graphics2D)result.getGraphics();

		setBackground(g2, result, image);

		Canvas canvas = new Canvas();
		Image tImage = image.getScaledInstance(width, height, quality);

		g2.drawImage(tImage, 0, 0, canvas);

		return result;
	}

	private static void setBackground(Graphics2D g2, BufferedImage result, BufferedImage source) {

		int targetType = result.getType();
		int sourceType = source.getType();

		if(targetType == BufferedImage.TYPE_INT_RGB && (sourceType == BufferedImage.TYPE_4BYTE_ABGR || sourceType == BufferedImage.TYPE_4BYTE_ABGR_PRE || sourceType == BufferedImage.TYPE_INT_ARGB || sourceType == BufferedImage.TYPE_INT_ARGB_PRE)){

			g2.setColor(Color.WHITE);
			g2.fillRect(0, 0, result.getWidth(), result.getHeight());
		}
	}

	public static BufferedImage changeImageType(BufferedImage image, int desiredImagetype, Color background) {

		if(image.getType() != desiredImagetype){

			BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), desiredImagetype);

			if(background != null){

				Graphics2D graphics2d = (Graphics2D)newImage.getGraphics();

				graphics2d.setColor(background);
				graphics2d.fillRect(0, 0, newImage.getWidth(), newImage.getHeight());

				graphics2d.drawImage(image, 0, 0, null);
			}

			return newImage;
		}

		return image;
	}

	public static boolean isImage(String filename) {

		String extension = FileUtils.getFileExtension(filename);

		if(extension != null){

			extension = extension.toLowerCase();

			if(extension.equals(ImageUtils.JPG) || extension.equals(ImageUtils.JPEG) || extension.equals(ImageUtils.GIF) || extension.equals(ImageUtils.PNG) || extension.equals(ImageUtils.BMP) || extension.equals(ImageUtils.WBMP)){

				return true;

			}

		}

		return false;

	}

	public static Dimension getImageDimensions(String path) throws FileNotFoundException, IOException {

		String suffix = FileUtils.getFileExtension(path);

		Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);

		if(iter.hasNext()){

			ImageReader reader = iter.next();
			ImageInputStream stream = null;

			try{
				stream = new FileImageInputStream(new File(path));
				reader.setInput(stream);
				int width = reader.getWidth(reader.getMinIndex());
				int height = reader.getHeight(reader.getMinIndex());
				return new Dimension(width, height);
			}finally{
				reader.dispose();

				if(stream != null){

					try{
						stream.close();
					}catch(IOException e){
					}
				}
			}
		}

		return null;
	}

	public static BufferedImage copyImage(BufferedImage image) {

		ColorModel cm = image.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = image.copyData(null);

		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	public static BufferedImage crop(BufferedImage image, int x1, int y1, int x2, int y2) {

		if(x1 < 0 || x2 <= x1 || y1 < 0 || y2 <= y1 || x2 > image.getWidth() || y2 > image.getHeight()){

			throw new IllegalArgumentException("Invalid crop coordinates");
		}

		int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();

		int newWidth = x2 - x1;

		int newHeight = y2 - y1;

		BufferedImage croppedImage = new BufferedImage(newWidth, newHeight, type);
		Graphics2D g = croppedImage.createGraphics();

		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setComposite(AlphaComposite.Src);

		g.drawImage(image, 0, 0, newWidth, newHeight, x1, y1, x2, y2, null);
		g.dispose();

		return croppedImage;

	}

	public static BufferedImage cropAsSquare(BufferedImage image) {

		if(image.getWidth() > image.getHeight()){

			int difference = image.getWidth() - image.getHeight();

			int factor = difference / 2;

			image = crop(image, factor, 0, image.getWidth() - factor, image.getHeight());

		}else if(image.getHeight() > image.getWidth()){

			int difference = image.getHeight() - image.getWidth();

			int factor = difference / 2;

			image = crop(image, 0, factor, image.getWidth(), image.getHeight() - factor);

		}

		return image;
	}

	public static boolean equals(BufferedImage img1, BufferedImage img2) {

		if(img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight()){

			for(int x = 0; x < img1.getWidth(); x++){

				for(int y = 0; y < img1.getHeight(); y++){

					if(img1.getRGB(x, y) != img2.getRGB(x, y)){

						return false;
					}
				}
			}

		}else{

			return false;
		}

		return true;
	}
}
