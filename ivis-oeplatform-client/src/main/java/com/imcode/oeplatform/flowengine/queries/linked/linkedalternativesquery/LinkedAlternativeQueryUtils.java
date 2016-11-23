package com.imcode.oeplatform.flowengine.queries.linked.linkedalternativesquery;

import com.imcode.oeplatform.flowengine.interfaces.LinkedMutableElement;
import com.imcode.oeplatform.flowengine.queries.linked.dropdownquery.LinkedDropDownAlternative;
import com.nordicpeak.flowengine.interfaces.ImmutableAlternative;
import com.nordicpeak.flowengine.interfaces.MutableAlternative;
import com.nordicpeak.flowengine.interfaces.QueryHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import se.unlogic.standardutils.xml.XMLUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinkedAlternativeQueryUtils {

	@SuppressWarnings("unchecked")
	public static <X extends LinkedAlternativesQuery> LinkedAlternativesQueryCallback<X> getGenericFixedAlternativesQueryCallback(Class<? extends LinkedAlternativesQuery> clazz, QueryHandler queryHandler, String queryTypeID) {

		return (LinkedAlternativesQueryCallback<X>) queryHandler.getQueryProvider(queryTypeID);
	}

	//todo пофиксить метод
	public static List<? extends Serializable> getAlternativeIDs(LinkedAlternativesQuery query) {

//		if (query.getAlternatives() == null) {
//
//			return null;
//		}
//
//		List<? extends Serializable> alternativeIDs = new ArrayList<Integer>(query.getAlternatives().size());
//
//		for (LinkedDropDownAlternative alternative : query.getAlternatives()) {
//
//			alternativeIDs.add(alternative.getId());
//		}
//
//		return alternativeIDs;
		throw new UnsupportedOperationException();
	}

	public static void clearAlternativeIDs(List<? extends LinkedDropDownAlternative> alternatives) {

		if (alternatives != null) {
			alternatives.stream().forEach(linkedDropDownAlternative -> linkedDropDownAlternative.setAlternativeID(null));
			for (MutableAlternative alternative : alternatives) {

				alternative.setAlternativeID(null);
			}
		}
	}

	public static Map<Integer, Integer> getAlternativeConversionMap(List<? extends ImmutableAlternative> alternatives, List<Integer> oldAlternativeIDs) {

		if(oldAlternativeIDs != null){

			HashMap<Integer, Integer> alternativeConversionMap = new HashMap<Integer, Integer>(oldAlternativeIDs.size());

			int index = 0;

			for(Integer oldAlternativeID : oldAlternativeIDs){

				alternativeConversionMap.put(oldAlternativeID, alternatives.get(index).getAlternativeID());

				index++;
			}

			return alternativeConversionMap;
		}

		return null;
	}

	//todo пофиксить метод
	public static void appendExportXMLAlternatives(Document doc, Element element, LinkedAlternativesQueryInstance queryInstance) {

//		if(queryInstance.getAlternatives() != null){
//
//			for(ImmutableAlternative alternative : queryInstance.getAlternatives()){
//
//				XMLUtils.appendNewElement(doc, element, "Value", alternative.getName());
//			}
//		}

//		if(queryInstance.getEntityClassname() != null){
//
//			XMLUtils.appendNewCDATAElement(doc, element, "TextAlternative", queryInstance.getEntityClassname());
//		}
	}
}
