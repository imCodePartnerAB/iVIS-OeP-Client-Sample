package se.unlogic.standardutils.populators;

import se.unlogic.standardutils.validation.NonNegativeStringIntegerValidator;


public class NonNegativeStringIntegerPopulator extends IntegerPopulator {

	public NonNegativeStringIntegerPopulator(){
		
		super(null,new NonNegativeStringIntegerValidator());
	}
}
