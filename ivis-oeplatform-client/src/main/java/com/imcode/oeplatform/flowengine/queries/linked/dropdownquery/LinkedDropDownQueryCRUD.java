package com.imcode.oeplatform.flowengine.queries.linked.dropdownquery;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.imcode.oeplatform.flowengine.populators.web.IvisAlternativesPopulator;
import com.imcode.oeplatform.flowengine.populators.web.LinkedAlternativesPopulator;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.dao.AnnotatedDAOWrapper;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationErrorType;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.webutils.http.BeanRequestPopulator;
import se.unlogic.webutils.http.URIParser;

import com.nordicpeak.flowengine.beans.QueryDescriptor;
import com.nordicpeak.flowengine.queries.basequery.BaseQueryCRUD;

public class LinkedDropDownQueryCRUD extends BaseQueryCRUD<LinkedDropDownQuery, LinkedDropDownQueryProviderModule> {

	protected AnnotatedDAOWrapper<LinkedDropDownQuery, Integer> queryDAO;
	
	protected static LinkedAlternativesPopulator<LinkedDropDownAlternative> ALTERNATIVES_POPLATOR = new LinkedAlternativesPopulator<>(LinkedDropDownAlternative.class);
	
	public LinkedDropDownQueryCRUD(AnnotatedDAOWrapper<LinkedDropDownQuery, Integer> queryDAO, BeanRequestPopulator<LinkedDropDownQuery> populator, String typeElementName, String typeLogName, String listMethodAlias, LinkedDropDownQueryProviderModule callback) {
		
		super(LinkedDropDownQuery.class, queryDAO, populator, typeElementName, typeLogName, listMethodAlias, callback);
		
		this.queryDAO = queryDAO;
	}

	@Override
	protected LinkedDropDownQuery populateFromUpdateRequest(LinkedDropDownQuery bean, HttpServletRequest req, User user, URIParser uriParser) throws ValidationException, Exception {
		
		LinkedDropDownQuery query = super.populateFromUpdateRequest(bean, req, user, uriParser);

		List<ValidationError> validationErrors = new ArrayList<ValidationError>();
		
		this.populateQueryDescriptor((QueryDescriptor) query.getQueryDescriptor(), req, validationErrors);
		
//		if(req.getParameter("useFreeTextAlternative") != null && StringUtils.isEmpty(bean.getFreeTextAlternative())) {
//			validationErrors.add(new ValidationError("freeTextAlternative", ValidationErrorType.RequiredField));
//		}
		
//		List<LinkedDropDownAlternative> alternatives = ALTERNATIVES_POPLATOR.populate(bean.getAlternatives(), req, validationErrors);
		
//		if(CollectionUtils.isEmpty(alternatives) || alternatives.size() + (bean.getFreeTextAlternative() != null ? 1 : 0) < 2) {
//			validationErrors.add(new ValidationError("ToFewAlternatives"));
//		}
		
		if(!validationErrors.isEmpty()) {
			throw new ValidationException(validationErrors);
		}
		
//		query.setAlternatives(alternatives);
		
		return query;
		
	}
	
	@Override
	protected List<Field> getBeanRelations() {
		
		return Arrays.asList(LinkedDropDownQuery.ALTERNATIVES_RELATION);
	}
	
}
