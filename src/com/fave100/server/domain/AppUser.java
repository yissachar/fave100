package com.fave100.server.domain;

import com.fave100.server.DAO;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.annotation.Entity;

@Entity
public class AppUser extends DatastoreObject{
	
	public static final Objectify ofy() {
		DAO dao = new DAO();
		return dao.ofy();
	}
	
	public static AppUser findAppUser(Long id) {
		return ofy().get(new Key<AppUser>(AppUser.class, id));
	}	
	
	public static String getLoginURL() {
		UserService userService = UserServiceFactory.getUserService();
		return userService.createLoginURL("/");
	}
	
	public AppUser persist() {
		ofy().put(this);
		return this;
	}
}
