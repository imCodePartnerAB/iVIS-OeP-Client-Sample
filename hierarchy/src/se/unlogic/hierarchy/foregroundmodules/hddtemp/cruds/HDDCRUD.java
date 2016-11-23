package se.unlogic.hierarchy.foregroundmodules.hddtemp.cruds;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.utils.IntegerBasedCRUD;
import se.unlogic.hierarchy.foregroundmodules.hddtemp.HDDTempModule;
import se.unlogic.standardutils.dao.CRUDDAO;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.xml.Elementable;
import se.unlogic.webutils.http.BeanRequestPopulator;
import se.unlogic.webutils.http.URIParser;


public class HDDCRUD<BeanType extends Elementable> extends IntegerBasedCRUD<BeanType,HDDTempModule> {

	public HDDCRUD(CRUDDAO<BeanType, Integer> crudDAO, BeanRequestPopulator<BeanType> populator, String typeElementName, String typeLogName, HDDTempModule hddTempModule) {

		super(crudDAO, populator, typeElementName, typeLogName, "", hddTempModule);
	}

	@Override
	public ForegroundModuleResponse list(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, List<ValidationError> validationErrors) throws Exception {

		return callback.list(req, res, user, uriParser, validationErrors);
	}
}
