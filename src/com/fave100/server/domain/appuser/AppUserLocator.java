package com.fave100.server.domain.appuser;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.web.bindery.requestfactory.shared.Locator;

/**
 * A locator class that RequestFactory will use to obtain instances of AppUser on the client.
 * Theoretically this is not needed, since all interaction with AppUser is done through AppUserDao service methods,
 * and not through RequestFactory instance methods, but RequestFactory insists on a locator.
 * 
 * @author yissachar.radcliffe
 * 
 */
public class AppUserLocator extends Locator<AppUser, String> {

	@Override
	public AppUser create(Class<? extends AppUser> clazz) {
		// No creation of AppUsers through client, interact only with the AppUser registration methods in AppUserDao
		return null;
	}

	@Override
	public AppUser find(Class<? extends AppUser> clazz, String id) {
		return ofy().load().type(AppUser.class).id(id.toLowerCase()).get();
	}

	@Override
	public Class<AppUser> getDomainType() {
		// Never called
		return null;
	}

	@Override
	public String getId(AppUser domainObject) {
		return domainObject.getId();
	}

	@Override
	public Class<String> getIdType() {
		return String.class;
	}

	@Override
	public Object getVersion(AppUser domainObject) {
		return domainObject.getVersion();
	}

}
