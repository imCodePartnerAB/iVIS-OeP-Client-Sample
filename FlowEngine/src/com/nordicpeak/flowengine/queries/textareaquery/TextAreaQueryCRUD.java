package com.nordicpeak.flowengine.queries.textareaquery;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.standardutils.dao.AnnotatedDAOWrapper;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.webutils.http.BeanRequestPopulator;
import se.unlogic.webutils.http.URIParser;

import com.nordicpeak.flowengine.beans.QueryDescriptor;
import com.nordicpeak.flowengine.queries.basequery.BaseQueryCRUD;

public class TextAreaQueryCRUD extends BaseQueryCRUD<TextAreaQuery, TextAreaQueryProviderModule> {

	protected AnnotatedDAOWrapper<TextAreaQuery, Integer> queryDAO;
	
	public TextAreaQueryCRUD(AnnotatedDAOWrapper<TextAreaQuery, Integer> queryDAO, BeanRequestPopulator<TextAreaQuery> populator, String typeElementName, String typeLogName, String listMethodAlias, TextAreaQueryProviderModule callback) {
		
		super(TextAreaQuery.class, queryDAO, populator, typeElementName, typeLogName, listMethodAlias, callback);
		
		this.queryDAO = queryDAO;
	}

	@Override
	protected TextAreaQuery populateFromUpdateRequest(TextAreaQuery bean, HttpServletRequest req, User user, URIParser uriParser) throws ValidationException, Exception {
		
		TextAreaQuery query = super.populateFromUpdateRequest(bean, req, user, uriParser);
		
		List<ValidationError> validationErrors = new ArrayList<ValidationError>();

		this.populateQueryDescriptor((QueryDescriptor) query.getQueryDescriptor(), req, validationErrors);
		
		Integer maxLength = query.getMaxLength();
		
		if(maxLength != null) {
			
			if(maxLength > 65535) {
				validationErrors.add(new ValidationError("MaxLengthToBig"));
			}
			
		}
		
		if(!validationErrors.isEmpty()) {
			throw new ValidationException(validationErrors);
		}

		return query;
	}

}
