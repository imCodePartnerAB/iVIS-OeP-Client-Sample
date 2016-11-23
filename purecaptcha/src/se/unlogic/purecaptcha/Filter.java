package se.unlogic.purecaptcha;

import java.awt.image.BufferedImage;


public interface Filter {

	public BufferedImage applyFilter(BufferedImage bufferedImage);
}
