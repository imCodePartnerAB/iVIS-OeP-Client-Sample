package se.unlogic.openhierarchy.foregroundmodules.siteprofile;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.exceptions.AccessDeniedException;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.settings.Setting;
import se.unlogic.hierarchy.core.utils.IntegerBasedCRUD;
import se.unlogic.openhierarchy.foregroundmodules.siteprofile.beans.Profile;
import se.unlogic.openhierarchy.foregroundmodules.siteprofile.beans.ProfileSettingValue;
import se.unlogic.openhierarchy.foregroundmodules.siteprofile.validationerrors.DomainAlreadyInUseValidationError;
import se.unlogic.standardutils.dao.CRUDDAO;
import se.unlogic.standardutils.factory.BeanFactory;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.populators.annotated.AnnotatedRequestPopulator;


public class ProfileCRUD extends IntegerBasedCRUD<Profile, SiteProfilesAdminModule> implements BeanFactory<ProfileSettingValue>{

	private static final AnnotatedRequestPopulator<Profile> POPULATOR = new AnnotatedRequestPopulator<Profile>(Profile.class);

	public ProfileCRUD(CRUDDAO<Profile, Integer> crudDAO, SiteProfilesAdminModule callback) {

		super(crudDAO, POPULATOR, "Profile", "profile", "", callback);
	}

	@Override
	public Profile getBean(Integer beanID, String getMode) throws SQLException, AccessDeniedException {

		if(getMode != null && getMode == SHOW){

			return callback.getProfile(beanID);
		}

		return super.getBean(beanID, getMode);
	}

	@Override
	protected List<Profile> getAllBeans(User user) throws SQLException {

		return callback.getProfiles();
	}

	@Override
	protected void validateAddPopulation(Profile bean, HttpServletRequest req, User user, URIParser uriParser) throws ValidationException, SQLException, Exception {

		List<ValidationError> validationErrors = new ArrayList<ValidationError>();

		if(bean.getDomains() != null){

			for(String domain : bean.getDomains()){

				Profile profile = callback.getProfile(domain);

				if(profile != null && !profile.equals(bean)){

					validationErrors.add(new DomainAlreadyInUseValidationError(domain, profile.getName()));
				}
			}
		}

		bean.setSettingValues(callback.getSettingValues(req, validationErrors, this, false));

		if(!validationErrors.isEmpty()){

			throw new ValidationException(validationErrors);
		}
	}


	@Override
	protected void validateUpdatePopulation(Profile bean, HttpServletRequest req, User user, URIParser uriParser) throws ValidationException, SQLException, Exception {

		validateAddPopulation(bean, req, user, uriParser);
	}

	@Override
	protected String getBeanName(Profile bean) {

		return bean.getName();
	}

	@Override
	public ProfileSettingValue newInstance() {

		return new ProfileSettingValue();
	}

	@Override
	protected void appendAddFormData(Document doc, Element addTypeElement, User user, HttpServletRequest req, URIParser uriParser) throws Exception {

		Set<Setting> settings = callback.getProfileSettings();
		
		XMLUtils.append(doc, addTypeElement, "SettingDescriptors", settings);
		callback.appendSettingHandler(doc, addTypeElement, req, callback.getGlobalSettingHandler(), settings);
		callback.appendDesigns(doc, addTypeElement);
	}

	@Override
	protected void appendUpdateFormData(Profile bean, Document doc, Element updateTypeElement, User user, HttpServletRequest req, URIParser uriParser) throws Exception {

		Set<Setting> settings = callback.getProfileSettings();
		
		XMLUtils.append(doc, updateTypeElement, "SettingDescriptors", settings);
		callback.appendSettingHandler(doc, updateTypeElement, req, callback.getProfile(bean.getProfileID()).getSettingHandler(), settings);
		callback.appendDesigns(doc, updateTypeElement);
	}

	@Override
	protected ForegroundModuleResponse beanAdded(Profile bean, HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		callback.cacheProfiles();

		return super.beanAdded(bean, req, res, user, uriParser);
	}

	@Override
	protected ForegroundModuleResponse beanUpdated(Profile bean, HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		callback.cacheProfiles();

		return super.beanUpdated(bean, req, res, user, uriParser);
	}

	@Override
	protected ForegroundModuleResponse beanDeleted(Profile bean, HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		callback.cacheProfiles();

		return super.beanDeleted(bean, req, res, user, uriParser);
	}

}
