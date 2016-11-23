package se.unlogic.standardutils.text;

import java.text.DecimalFormat;

import se.unlogic.standardutils.factory.BeanFactory;


public class DecimalFormatFactory implements BeanFactory<DecimalFormat> {

	protected String format;

	public DecimalFormatFactory(String format) {

		this.format = format;
	}

	public DecimalFormat newInstance() {

		return new DecimalFormat(format);
	}
}
