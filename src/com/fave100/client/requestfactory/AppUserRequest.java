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

	Request<Boolean> isGoogleUserLoggedIn();
	Request<String> getLoginLogoutURL(String destinationURL);
	Request<AppUserProxy> createAppUserFromGoogleAccount(String username);
	
	Request<Void> followUser(String username);
	Request<Boolean> checkFollowing(String username);
	
	Request<Void> removeFaveItemForCurrentUser(int index);
	Request<Void> addFaveItemForCurrentUser(Long songID, SongProxy songProxy);
	Request<Void> rerankFaveItemForCurrentUser(int currentIndex, int newIndex);
	Request<List<SongProxy>> getMasterFaveList();
	Request<AppUserProxy> login(String username, String password);
	Request<Void> logout();
	Request<AppUserProxy> createAppUser(String username, String password, String email);
	Request<List<FaveItemProxy>> getFaveItemsForCurrentUser();
	
	Request<Boolean> checkPassword(String password);
}