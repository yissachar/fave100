package com.fave100.client.requestfactory;

import com.fave100.server.domain.AppUser;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;

@Service(AppUser.class)
public interface AppUserRequest extends RequestContext{	
	Request<AppUserProxy> getLoggedInAppUser();
	Request<Boolean> isGoogleUserLoggedIn();
	Request<AppUserProxy> createAppUserFromCurrentGoogleUser(String username);
}