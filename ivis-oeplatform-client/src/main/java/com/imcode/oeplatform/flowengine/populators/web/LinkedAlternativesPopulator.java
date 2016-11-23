package com.imcode.oeplatform.flowengine.populators.web;

import com.imcode.oeplatform.flowengine.interfaces.LinkedMutableElement;
import com.imcode.oeplatform.flowengine.queries.linked.dropdownquery.LinkedDropDownAlternative;
import com.nordicpeak.flowengine.interfaces.MutableAlternative;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLParser;
import se.unlogic.webutils.validation.ValidationUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LinkedAlternativesPopulator<ALTERNATIVE_TYPE extends LinkedDropDownAlternative> {

	private Class<ALTERNATIVE_TYPE> alternativeClass;

	public LinkedAlternativesPopulator(Class<ALTERNATIVE_TYPE> alternativeClass) {
		this.alternativeClass = alternativeClass;
	}
	
	public List<ALTERNATIVE_TYPE> populate(List<ALTERNATIVE_TYPE> currentAlternatives, HttpServletRequest req, List<ValidationError> validationErrors) {
		
//		String[] alternativeIDs = req.getParameterValues("alternativeID");
//
//		List<ALTERNATIVE_TYPE> alternatives = new ArrayList<ALTERNATIVE_TYPE>();
//
//		if(alternativeIDs != null) {
//
//			for(String alternativeID : alternativeIDs) {
//
//				//TODO validate max length
//				String name = ValidationUtils.validateNotEmptyParameter("alternative_" + alternativeID, req, validationErrors);
//
//				String sortOrder = req.getParameter("sortorder_" + alternativeID);
//
//				boolean exported = Objects.nonNull(req.getParameter("exported_" + alternativeID));
//
//				String xsdElementName = "";
//
//				if (exported) {
//					xsdElementName = ValidationUtils.validateNotEmptyParameter("xsdElementName_" + alternativeID, req, validationErrors);
//				} else {
//					xsdElementName = req.getParameter("xsdElementName_" + alternativeID);
//					xsdElementName = Objects.nonNull(xsdElementName) ? xsdElementName.trim() : "";
//				}
//
//				if(!StringUtils.isEmpty(name) && NumberUtils.isInt(sortOrder)) {
//
//					ALTERNATIVE_TYPE alternative = this.getNewAlternativeInstance();
//
//					alternative.setName(name);
//					alternative.setSortIndex(NumberUtils.toInt(sortOrder));
//					alternative.setExported(exported);
//					alternative.setXsdElementName(xsdElementName);
//
//					if(NumberUtils.isInt(alternativeID)) {
//
//						this.checkForExistingAlternatives(currentAlternatives, alternative, NumberUtils.toInt(alternativeID));
//
//					}
//
//					alternatives.add(alternative);
//
//				}
//			}
//
//		}
//
//		return alternatives;
				throw new UnsupportedOperationException();
	}
	
	public List<ALTERNATIVE_TYPE> populate(XMLParser xmlParser, List<ValidationError> errors) throws ValidationException {

//		List<XMLParser> xmlParsers = xmlParser.getNodes("Alternatives/" + alternativeClass.getSimpleName());
//
//		if(CollectionUtils.isEmpty(xmlParsers)) {
//
//			errors.add(new ValidationError("NoAlternativesFound"));
//
//			return null;
//
//		}
//
//		List<ALTERNATIVE_TYPE> alternatives = new ArrayList<ALTERNATIVE_TYPE>();
//
//		for(XMLParser parser : xmlParsers) {
//
//			ALTERNATIVE_TYPE alternative = this.getNewAlternativeInstance();
//			alternative.populate(parser);
//			alternatives.add(alternative);
//
//		}
//
//		return alternatives;
		throw new UnsupportedOperationException();

	}
	
	protected void checkForExistingAlternatives(List<ALTERNATIVE_TYPE> currentAlternatives, ALTERNATIVE_TYPE alternative, Integer alternativeID) {
//
//		if (!CollectionUtils.isEmpty(currentAlternatives)) {
//
//			for (MutableAlternative queryAlternative : currentAlternatives) {
//
//				if (queryAlternative.getAlternativeID().equals(alternativeID)) {
//
//					alternative.setAlternativeID(alternativeID);
//					break;
//
//				}
//
//			}
//
//		}

	}
	
	protected ALTERNATIVE_TYPE getNewAlternativeInstance() {
//
//		try {
//
//			return alternativeClass.newInstance();
//
//		} catch (InstantiationException e) {
//
//			throw new RuntimeException(e);
//
//		} catch (IllegalAccessException e) {
//
//			throw new RuntimeException(e);
//		}
		throw new UnsupportedOperationException();
	}
	
}
