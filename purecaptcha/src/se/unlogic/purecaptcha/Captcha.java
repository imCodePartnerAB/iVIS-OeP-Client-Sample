package se.unlogic.purecaptcha;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import se.unlogic.purecaptcha.textgenerators.TextGenerator;
import se.unlogic.purecaptcha.wordrenderers.WordRenderer;


public class Captcha {

	protected TextGenerator textGenerator;
	protected WordRenderer wordRenderer;
	protected List<Filter> filters;
	protected int width;
	protected int height;
	
	public Captcha(Config config){
		
		this.textGenerator = config.getTextGenerator();
		
		if(textGenerator == null){
			
			throw new CaptchaConfigurationException("TextGenerator cannot be null!");
		}
		
		this.wordRenderer = config.getWordRenderer();
		
		if(wordRenderer == null){
			
			throw new CaptchaConfigurationException("WordRenderer cannot be null!");
		}
		
		this.width = config.getWidth();
		
		if(width <= 0){
			
			throw new CaptchaConfigurationException("Width cannot be smaller than 1!");
		}
		
		this.height = config.getHeight();
		
		if(height <= 0){
			
			throw new CaptchaConfigurationException("Height cannot be smaller than 1!");
		}
		
		if(config.getFilters() != null){
			
			this.filters = new ArrayList<Filter>(config.getFilters());	
		}
	}
	
	public CaptchaImage generateCaptchaImage(){
		
		String text = textGenerator.getText();
		
		BufferedImage bufferedImage = wordRenderer.renderWord(text, width, height);
		
		if(filters != null){
			
			for(Filter filter : filters){
				
				bufferedImage = filter.applyFilter(bufferedImage);
			}
		}
				
		return new CaptchaImage(bufferedImage, text);
	}
}
