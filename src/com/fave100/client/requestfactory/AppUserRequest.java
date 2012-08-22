package com.fave100.client.requestfactory;

import java.util.List;

import com.fave100.server.domain.AppUser;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;

@Service(AppUser.class)
public interface AppUserRequest extends RequestContext{	
	Request<AppUserProxy> findAppUser(String username);
	Request<List<AppUserProxy>> getAppUsers();
	Request<AppUserProxy> getLoggedInAppUser();
	
	Request<AppUserProxy> login(String username, String password);
	Request<Boolean> checkPassword(String password);	
	Request<Void> logout();
	Request<AppUserProxy> createAppUser(String username, String password, String email);

	Request<AppUserProxy> loginWithGoogle();
	Request<Boolean> isGoogleUserLoggedIn();
	Request<String> getGoogleLoginURL(String destinationURL);
	Request<String> getGoogleLogoutURL(String destinationURL);
	Request<String> getGoogleLoginLogoutURL(String destinationURL);
	Request<AppUserProxy> createAppUserFromGoogleAccount(String username);
	
	Request<AppUserProxy> loginWithTwitter();
	Request<String> getTwitterAuthUrl();	
	Request<Boolean> isTwitterUserLoggedIn();
	
	Request<Void> followUser(String username);
	Request<Boolean> checkFollowing(String username);
	
	Request<List<String>> getFaveFeedForCurrentUser();
	Request<List<String>> getActivityForUser(String username);
}