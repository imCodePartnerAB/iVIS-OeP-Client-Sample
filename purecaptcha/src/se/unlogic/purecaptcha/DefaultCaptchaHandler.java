package se.unlogic.purecaptcha;

import java.awt.Color;
import java.util.ArrayList;

import se.unlogic.purecaptcha.filters.DefaultBackground;
import se.unlogic.purecaptcha.filters.DefaultNoise;
import se.unlogic.purecaptcha.filters.WaterRipple;
import se.unlogic.purecaptcha.textgenerators.DefaultTextGenerator;
import se.unlogic.purecaptcha.wordrenderers.DefaultWordRenderer;


public class DefaultCaptchaHandler extends CaptchaHandler {

	public DefaultCaptchaHandler(String sessionAttribute, long validationTimeout, boolean caseSensitive) {

		super(new Captcha(getDefaultConfig(5)), sessionAttribute, validationTimeout, caseSensitive);
	}
	
	public DefaultCaptchaHandler(String sessionAttribute, long validationTimeout, boolean caseSensitive, int characters) {

		super(new Captcha(getDefaultConfig(characters)), sessionAttribute, validationTimeout, caseSensitive);
	}

	private static Config getDefaultConfig(int characters) {

		SimpleConfig config = new SimpleConfig();
		
		config.setHeight(60);
		config.setWidth(200);
		
		config.setTextGenerator(new DefaultTextGenerator(characters, DefaultTextGenerator.BIG_LETTERS));
		config.setWordRenderer(new DefaultWordRenderer(DefaultWordRenderer.getJavaDefaultFonts(40),Color.BLACK));
		
		ArrayList<Filter> filters = new ArrayList<Filter>();
		
		filters.add(new WaterRipple());
		filters.add(DefaultNoise.getDefaultNoiseTypeTwo(Color.GRAY));
		filters.add(new DefaultBackground(Color.LIGHT_GRAY,Color.WHITE));
		
		config.setFilters(filters);
		
		return config;
	}
}
