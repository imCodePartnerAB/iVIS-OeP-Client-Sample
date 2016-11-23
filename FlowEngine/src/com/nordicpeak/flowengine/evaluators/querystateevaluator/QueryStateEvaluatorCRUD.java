package com.nordicpeak.flowengine.evaluators.querystateevaluator;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.standardutils.dao.AnnotatedDAOWrapper;
import se.unlogic.standardutils.populators.EnumPopulator;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.populators.annotated.AnnotatedRequestPopulator;

import com.nordicpeak.flowengine.evaluators.baseevaluator.BaseEvaluatorCRUD;
import com.nordicpeak.flowengine.interfaces.ImmutableAlternative;
import com.nordicpeak.flowengine.interfaces.ImmutableFlow;
import com.nordicpeak.flowengine.interfaces.ImmutableQueryDescriptor;
import com.nordicpeak.flowengine.interfaces.ImmutableStep;
import com.nordicpeak.flowengine.interfaces.MutableQueryDescriptor;
import com.nordicpeak.flowengine.queries.fixedalternativesquery.FixedAlternativesQuery;


public class QueryStateEvaluatorCRUD extends BaseEvaluatorCRUD<QueryStateEvaluator, QueryStateEvaluationProviderModule> {

	private static final AnnotatedRequestPopulator<QueryStateEvaluator> POPULATOR = new AnnotatedRequestPopulator<QueryStateEvaluator>(QueryStateEvaluator.class);

	protected static EnumPopulator<SelectionMode> SELECTIONMODE_POPULATOR = new EnumPopulator<SelectionMode>(SelectionMode.class);
	
	public QueryStateEvaluatorCRUD(Class<QueryStateEvaluator> beanClass, AnnotatedDAOWrapper<QueryStateEvaluator, Integer> evaluatorDAO, QueryStateEvaluationProviderModule callback) {

		super(beanClass, evaluatorDAO, POPULATOR, "QueryStateEvaluator", "query state evaluator", null, callback);
	}

	@Override
	protected void appendUpdateFormData(QueryStateEvaluator bean, Document doc, Element updateTypeElement, User user, HttpServletRequest req, URIParser uriParser) throws Exception {

		FixedAlternativesQuery query = (FixedAlternativesQuery)callback.getQueryHandler().getQuery((MutableQueryDescriptor)bean.getEvaluatorDescriptor().getQueryDescriptor());

		List<? extends ImmutableAlternative> alternatives = query.getAlternatives();

		if(alternatives != null){

			Element alternativesElement = doc.createElement("Alternatives");
			updateTypeElement.appendChild(alternativesElement);

			for(ImmutableAlternative alternative : alternatives){

				Element alternativeElement = doc.createElement("Alternative");
				alternativesElement.appendChild(alternativeElement);

				XMLUtils.appendNewElement(doc, alternativeElement, "alternativeID", alternative.getAlternativeID());
				XMLUtils.appendNewCDATAElement(doc, alternativeElement, "name", alternative.getName());
			}
		}

		ImmutableFlow flow = callback.getFlowAdminModule().getFlow(bean.getEvaluatorDescriptor().getQueryDescriptor().getStep().getFlow().getFlowID());

		updateTypeElement.appendChild(flow.toXML(doc));

		Element disabledQueriesElement = doc.createElement("DisabledQueries");
		updateTypeElement.appendChild(disabledQueriesElement);

		outer: for(ImmutableStep step : flow.getSteps()){

			if(step.getQueryDescriptors() == null){

				continue;
			}

			Integer queryID = bean.getEvaluatorDescriptor().getQueryDescriptor().getQueryID();

			for(ImmutableQueryDescriptor queryDescriptor : step.getQueryDescriptors()){

				XMLUtils.appendNewElement(doc, disabledQueriesElement, "queryID", queryDescriptor.getQueryID());

				if(queryDescriptor.getQueryID().equals(queryID)){

					break outer;
				}
			}
		}
	}
}
