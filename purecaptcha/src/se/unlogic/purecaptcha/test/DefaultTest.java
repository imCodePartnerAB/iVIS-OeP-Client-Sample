package se.unlogic.purecaptcha.test;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import se.unlogic.purecaptcha.Captcha;
import se.unlogic.purecaptcha.CaptchaImage;
import se.unlogic.purecaptcha.Filter;
import se.unlogic.purecaptcha.SimpleConfig;
import se.unlogic.purecaptcha.filters.DefaultBackground;
import se.unlogic.purecaptcha.filters.DefaultNoise;
import se.unlogic.purecaptcha.filters.WaterRipple;
import se.unlogic.purecaptcha.textgenerators.DefaultTextGenerator;
import se.unlogic.purecaptcha.wordrenderers.DefaultWordRenderer;


public class DefaultTest {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {

		SimpleConfig config = new SimpleConfig();
		
		config.setHeight(80);
		config.setWidth(250);
		
		config.setTextGenerator(new DefaultTextGenerator(5, DefaultTextGenerator.BIG_LETTERS));

		//config.setWordRenderer(new DefaultWordRenderer(DefaultWordRenderer.getDefaultFonts(50),Color.BLACK));
		config.setWordRenderer(new DefaultWordRenderer(DefaultWordRenderer.getJavaDefaultFonts(50),Color.BLACK));
		
		ArrayList<Filter> filters = new ArrayList<Filter>();
		
		//filters.add(new ShadowGimpy());
		filters.add(new WaterRipple());
		//filters.add(new FishEyeGimpy(Color.BLACK,Color.BLACK,5,3));
		filters.add(DefaultNoise.getDefaultNoiseTypeTwo(Color.GRAY));
		filters.add(new DefaultBackground(Color.LIGHT_GRAY,Color.WHITE));
		
		config.setFilters(filters);
		
		Captcha captcha = new Captcha(config);
		
		while(true){
			
			CaptchaImage captchaImage = captcha.generateCaptchaImage();
			
			System.out.println("Text: " + captchaImage.getCode());
			
			ImageIO.write(captchaImage.getBufferedImage(), "jpg", new File("captcha.jpg"));
			
			Thread.sleep(1000);
		}
		
	}

}
