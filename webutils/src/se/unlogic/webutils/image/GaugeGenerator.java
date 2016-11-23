/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.webutils.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import se.unlogic.standardutils.image.ImageUtils;


public class GaugeGenerator {

	public static final BufferedImage PERCENT_GAUGE;

	static{
		try {
			System.setProperty("java.awt.headless", "true");
			PERCENT_GAUGE = ImageIO.read(GaugeGenerator.class.getResource("resources/percent_gauage.png"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	public static void getPercentGauge(float percent, HttpServletResponse res) throws IOException{

		BufferedImage bufferedImage = generatePercentGauge(percent, null);

		res.setContentType("image/png");

		ImageIO.write(bufferedImage, ImageUtils.PNG, res.getOutputStream());
	}

	public static void getPercentGauge(float percent, HttpServletResponse res, Integer width) throws IOException{

		BufferedImage bufferedImage = generatePercentGauge(percent, width);

		res.setContentType("image/png");

		ImageIO.write(bufferedImage, ImageUtils.PNG, res.getOutputStream());
	}

	public static BufferedImage generatePercentGauge(float percent, Integer width) throws IOException{

		if(percent < 0){

			percent = 0f;

		}else if(percent > 100){

			percent = 100f;
		}

		BufferedImage bufferedImage = new BufferedImage(PERCENT_GAUGE.getWidth(), PERCENT_GAUGE.getHeight(), BufferedImage.TYPE_INT_ARGB);

		Graphics2D graphics = bufferedImage.createGraphics();

		graphics.drawImage(PERCENT_GAUGE, 1, 1, null);

		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		Polygon polygon = new Polygon();

		polygon.addPoint(100, 15);
		polygon.addPoint(96, 100);
		polygon.addPoint(104, 100);

		AffineTransform transformer = AffineTransform.getRotateInstance((((percent*3f)+210)*Math.PI)/180, 100, 100);

		Shape shape = transformer.createTransformedShape(polygon);


		if(percent < 80){

			graphics.setColor(Color.GREEN);

		}else if(percent >= 80 && percent < 90){

			graphics.setColor(Color.YELLOW);

		}else{

			graphics.setColor(Color.RED);
		}


		graphics.fill(shape);

		if(width != null){

			bufferedImage = ImageUtils.scaleImageByWidth(bufferedImage, width, Image.SCALE_SMOOTH, BufferedImage.TYPE_INT_ARGB);
		}

		return bufferedImage;
	}
}
