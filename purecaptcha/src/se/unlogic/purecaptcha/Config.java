package se.unlogic.purecaptcha;

import java.util.List;

import se.unlogic.purecaptcha.textgenerators.TextGenerator;
import se.unlogic.purecaptcha.wordrenderers.WordRenderer;

public interface Config {

	public TextGenerator getTextGenerator();

	public WordRenderer getWordRenderer();

	public List<Filter> getFilters();
 
	public int getWidth();

	public int getHeight();

}