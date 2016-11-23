package se.unlogic.purecaptcha;

import java.util.List;

import se.unlogic.purecaptcha.textgenerators.TextGenerator;
import se.unlogic.purecaptcha.wordrenderers.WordRenderer;


public class SimpleConfig implements Config {

	protected TextGenerator textGenerator;
	protected WordRenderer wordRenderer;
	protected List<Filter> filters;
	protected int width;
	protected int height;
		
	/* (non-Javadoc)
	 * @see se.unlogic.purecaptcha.Config#getTextProducer()
	 */
	public TextGenerator getTextGenerator() {
	
		return textGenerator;
	}
	
	public void setTextGenerator(TextGenerator textGenerator) {
	
		this.textGenerator = textGenerator;
	}
	
	/* (non-Javadoc)
	 * @see se.unlogic.purecaptcha.Config#getWordRenderer()
	 */
	public WordRenderer getWordRenderer() {
	
		return wordRenderer;
	}
	
	public void setWordRenderer(WordRenderer wordRenderer) {
	
		this.wordRenderer = wordRenderer;
	}
	
	/* (non-Javadoc)
	 * @see se.unlogic.purecaptcha.Config#getFilters()
	 */
	public List<Filter> getFilters() {
	
		return filters;
	}
	
	public void setFilters(List<Filter> filters) {
	
		this.filters = filters;
	}
	
	/* (non-Javadoc)
	 * @see se.unlogic.purecaptcha.Config#getWidth()
	 */
	public int getWidth() {
	
		return width;
	}
	
	public void setWidth(int width) {
	
		this.width = width;
	}
	
	/* (non-Javadoc)
	 * @see se.unlogic.purecaptcha.Config#getHeight()
	 */
	public int getHeight() {
	
		return height;
	}
	
	public void setHeight(int height) {
	
		this.height = height;
	}
	
	
}
