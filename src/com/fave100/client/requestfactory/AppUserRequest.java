package com.fave100.client.requestfactory;

import java.util.List;

import com.fave100.server.domain.AppUser;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;

@Service(AppUser.class)
public interface AppUserRequest extends RequestContext{	
	Request<AppUserProxy> getLoggedInAppUser();
	Request<Boolean> isGoogleUserLoggedIn();
	Request<AppUserProxy> createAppUserFromCurrentGoogleUser(String username);
	Request<Void> removeFaveItemForCurrentUser(int index);
	Request<Void> addFaveItemForCurrentUser(Long valueOf, SongProxy songProxy);
	Request<List<FaveItemProxy>> getAllSongsForCurrentUser();
	Request<Void> rerankFaveItemForCurrentUser(int currentIndex, int newIndex);
	Request<List<FaveItemProxy>> getMasterFaveList();
}