package se.unlogic.purecaptcha.textgenerators;

import java.util.Random;

/**
 * {@link DefaultTextGenerator} creates random text from an array of characters
 * with specified length.
 */
public class DefaultTextGenerator implements TextGenerator
{
	public static final char[] BIG_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	public static final char[] SMALL_LETTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
	public static final char[] MIXED_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
	
	protected int length;
	protected char[] chars;
	
	public DefaultTextGenerator(int length, char[] chars) {

		super();
		this.length = length;
		this.chars = chars;
	}

	/**
	 * @return the random text
	 */
	public String getText()
	{
		int randomContext = chars.length - 1;
		Random rand = new Random();
		StringBuffer text = new StringBuffer();
		for (int i = 0; i < length; i++)
		{
			text.append(chars[rand.nextInt(randomContext) + 1]);
		}

		return text.toString();
	}
}
