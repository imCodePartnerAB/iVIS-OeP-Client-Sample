package se.unlogic.standardutils.populators;

import se.unlogic.standardutils.populators.IntegerPopulator;
import se.unlogic.standardutils.validation.PositiveStringIntegerValidator;


public class PositiveStringIntegerPopulator extends IntegerPopulator {

	public PositiveStringIntegerPopulator(){
		
		super(null,new PositiveStringIntegerValidator());
	}
}
