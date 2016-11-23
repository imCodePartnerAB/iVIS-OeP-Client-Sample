package com.nordicpeak.flowengine.populators;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLParser;
import se.unlogic.webutils.validation.ValidationUtils;

import com.nordicpeak.flowengine.interfaces.MutableAlternative;

public class AlternativesPopulator<AlternativeType extends MutableAlternative> {

	private Class<AlternativeType> alternativeClass;
	
	public AlternativesPopulator(Class<AlternativeType> alternativeClass) {
		this.alternativeClass = alternativeClass;
	}
	
	public List<AlternativeType> populate(List<AlternativeType> currentAlternatives, HttpServletRequest req, List<ValidationError> validationErrors) {
		
		String[] alternativeIDs = req.getParameterValues("alternativeID");
		
		List<AlternativeType> alternatives = new ArrayList<AlternativeType>();
		
		if(alternativeIDs != null) {
			
			for(String alternativeID : alternativeIDs) {
				
				//TODO validate max length
				String name = ValidationUtils.validateNotEmptyParameter("alternative_" + alternativeID, req, validationErrors);
				
				String sortOrder = req.getParameter("sortorder_" + alternativeID);

				if(!StringUtils.isEmpty(name) && NumberUtils.isInt(sortOrder)) {
				
					AlternativeType alternative = this.getNewAlternativeInstance();
					
					alternative.setName(name);
					alternative.setSortIndex(NumberUtils.toInt(sortOrder));
					
					if(NumberUtils.isInt(alternativeID)) {
						
						this.checkForExistingAlternatives(currentAlternatives, alternative, NumberUtils.toInt(alternativeID));
						
					}
				
					alternatives.add(alternative);
					
				}
			}
			
		}
		
		return alternatives;
		
	}
	
	public List<AlternativeType> populate(XMLParser xmlParser, List<ValidationError> errors) throws ValidationException {
		
		List<XMLParser> xmlParsers = xmlParser.getNodes("Alternatives/" + alternativeClass.getSimpleName());
		
		if(CollectionUtils.isEmpty(xmlParsers)) {
			
			errors.add(new ValidationError("NoAlternativesFound"));
			
			return null;
			
		}
		
		List<AlternativeType> alternatives = new ArrayList<AlternativeType>();
		
		for(XMLParser parser : xmlParsers) {
			
			AlternativeType alternative = this.getNewAlternativeInstance();
			alternative.populate(parser);
			alternatives.add(alternative);
			
		}
		
		return alternatives;
		
	}
	
	protected void checkForExistingAlternatives(List<AlternativeType> currentAlternatives, AlternativeType alternative, Integer alternativeID) {
		
		if (!CollectionUtils.isEmpty(currentAlternatives)) {
			
			for (MutableAlternative queryAlternative : currentAlternatives) {
				
				if (queryAlternative.getAlternativeID().equals(alternativeID)) {
					
					alternative.setAlternativeID(alternativeID);
					break;
			
				}
				
			}
			
		}

	}
	
	protected AlternativeType getNewAlternativeInstance() {
		
		try {
			
			return alternativeClass.newInstance();

		} catch (InstantiationException e) {

			throw new RuntimeException(e);

		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);
		}
		
	}
	
}
