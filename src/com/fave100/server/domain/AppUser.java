package com.fave100.server.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;

import com.fave100.client.requestfactory.FaveItemProxy;
import com.fave100.server.DAO;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.annotation.Entity;

@Entity
public class AppUser extends DatastoreObject{
	
	private String name;
	private String googleId;
	private String email;
	
	public static final Objectify ofy() {
		DAO dao = new DAO();
		return dao.ofy();
	}
	
	public static AppUser findAppUser(Long id) {
		return ofy().get(new Key<AppUser>(AppUser.class, id));
	}
	
	public static AppUser findAppUserByGoogleId(String googleId) {
		return ofy().query(AppUser.class).filter("googleId", googleId).get();		
	}
	
	public static AppUser findLoggedInAppUser() {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if(user != null) {
			AppUser appUser = findAppUserByGoogleId(user.getUserId());
			if(appUser != null) {
				return appUser;
			} else {
				return createCurrentUser();
			}
		} else {
			return null;
		}
	}
	
	public static String getLoginLogoutURL(String redirect) {
		UserService userService = UserServiceFactory.getUserService();
		if(userService.getCurrentUser() != null) {			
			return userService.createLogoutURL(redirect);
		} else {
			return userService.createLoginURL(redirect);
		}
	}
	
	public static AppUser createCurrentUser() {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		
		AppUser appUser = new AppUser();
		appUser.setName(user.getNickname());
		appUser.setEmail(user.getEmail());
		appUser.setGoogleId(user.getUserId());
		return(appUser.persist());
	}
	
	public AppUser persist() {
		ofy().put(this);
		return this;
	}

	public String getGoogleId() {
		return googleId;
	}

	public void setGoogleId(String googleId) {
		this.googleId = googleId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
