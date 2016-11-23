package se.unlogic.hierarchy.foregroundmodules.registration;

import javax.servlet.http.HttpServletRequest;

import se.unlogic.emailutils.populators.EmailPopulator;
import se.unlogic.hierarchy.foregroundmodules.userproviders.SimpleUser;
import se.unlogic.standardutils.string.BeanTagSourceFactory;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.webutils.populators.annotated.AnnotatedRequestPopulator;

public class SimpleUserModalRegistrationModule extends BaseModalRegistrationModule<SimpleUser> {

	private static final AnnotatedRequestPopulator<SimpleUser> POPULATOR = new AnnotatedRequestPopulator<SimpleUser>(SimpleUser.class, new EmailPopulator());
	
	@Override
	protected BeanTagSourceFactory<SimpleUser> createUserBeanTagSourceFactory() {

		BeanTagSourceFactory<SimpleUser> tagSourceFactory = new BeanTagSourceFactory<SimpleUser>(SimpleUser.class);

		tagSourceFactory.addAllFields("$user.","groups");

		return tagSourceFactory;
	}

	@Override
	protected SimpleUser populate(HttpServletRequest req) throws ValidationException {

		return POPULATOR.populate(req);
	}
}
