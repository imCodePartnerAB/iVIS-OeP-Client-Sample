package se.unlogic.purecaptcha.filters;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import se.unlogic.purecaptcha.Filter;

import com.jhlabs.image.RippleFilter;
import com.jhlabs.image.ShadowFilter;
import com.jhlabs.image.TransformFilter;

/**
 * {@link ShadowGimpy} adds shadow to the text on the image and two noises.
 */
public class ShadowGimpy implements Filter {

	/**
	 * Applies distortion by adding shadow to the text and also two noises.
	 * 
	 * @param baseImage the base image
	 * @return the distorted image
	 */
	public BufferedImage applyFilter(BufferedImage baseImage) {

		BufferedImage distortedImage = new BufferedImage(baseImage.getWidth(), baseImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

		Graphics2D graph = (Graphics2D) distortedImage.getGraphics();

		ShadowFilter shadowFilter = new ShadowFilter();
		shadowFilter.setRadius(10);
		shadowFilter.setDistance(5);
		shadowFilter.setOpacity(1);

		Random rand = new Random();

		RippleFilter rippleFilter = new RippleFilter();
		rippleFilter.setWaveType(RippleFilter.SINE);
		rippleFilter.setXAmplitude(7.6f);
		rippleFilter.setYAmplitude(rand.nextFloat() + 1.0f);
		rippleFilter.setXWavelength(rand.nextInt(7) + 8);
		rippleFilter.setYWavelength(rand.nextInt(3) + 2);
		rippleFilter.setEdgeAction(TransformFilter.BILINEAR);

		BufferedImage effectImage = rippleFilter.filter(baseImage, null);
		effectImage = shadowFilter.filter(effectImage, null);

		graph.drawImage(effectImage, 0, 0, null, null);
		graph.dispose();

		return distortedImage;
	}
}
