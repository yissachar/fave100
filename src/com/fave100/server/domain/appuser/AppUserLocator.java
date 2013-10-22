package com.fave100.server.domain.appuser;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.web.bindery.requestfactory.shared.Locator;

public class AppUserLocator extends Locator<AppUser, String> {

	@Override
	public AppUser create(Class<? extends AppUser> clazz) {
		// No creation of AppUsers through client, interact only with the AppUser registration methods
		return null;
	}

	@Override
	public AppUser find(Class<? extends AppUser> clazz, String id) {
		return ofy().load().type(AppUser.class).id(id.toLowerCase()).get();
	}

	@Override
	public Class<AppUser> getDomainType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getId(AppUser domainObject) {
		return domainObject.getId();
	}

	@Override
	public Class<String> getIdType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getVersion(AppUser domainObject) {
		return domainObject.getVersion();
	}

}
