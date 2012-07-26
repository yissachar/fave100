package com.fave100.server.domain;

import javax.persistence.Id;
import javax.persistence.PrePersist;

import com.fave100.server.DAO;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.annotation.Entity;

/**
 * A Fave100 user.
 * @author yissachar.radcliffe
 *
 */
@Entity
public class AppUser{
	
	@Id private String username;
	private Integer version = 0;
	private String googleId;
	private String email;
	
	public static final Objectify ofy() {
		//TODO: find better way of getting Objectify instance
		DAO dao = new DAO();
		return dao.ofy();
	}
	
	public static AppUser findAppUser(String username) {
		return ofy().get(new Key<AppUser>(AppUser.class, username));
	}
	
	public static AppUser findAppUserByGoogleId(String googleId) {
		return ofy().query(AppUser.class).filter("googleId", googleId).get();		
	}
	
	public static boolean isGoogleUserLoggedIn() {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if(user != null) {
			return true;
		}
		return false;
	}
	
	public static AppUser getLoggedInAppUser() {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if(user != null) {
			AppUser appUser = findAppUserByGoogleId(user.getUserId());
			if(appUser != null) {
				return appUser;
			} else {
				//TODO: Create sign up page instead of silently creating new user
				return createCurrentUser();
			}
		} else {
			return null;
		}
	}	
	
	public static AppUser createCurrentUser() {
		//TODO: should return the current user if it already exists
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		AppUser appUser = new AppUser();
		appUser.setUsername(user.getNickname());
		appUser.setEmail(user.getEmail());
		appUser.setGoogleId(user.getUserId());
		return(appUser.persist());
		// TODO: Use transactions to prevent duplicate user entries
		/*Transaction txn = ofy().getTxn();
		try {
			// Verify that username is unique
			//if(ofy().find(AppUser.class, user.getNickname()) != null) {
			//	return null; 
			//} 
			AppUser appUser = new AppUser();
			appUser.setUsername(user.getNickname());
			appUser.setEmail(user.getEmail());
			appUser.setGoogleId(user.getUserId());
			AppUser foo = appUser.persist();
			txn.commit();
			return(foo);
		} finally {
			if(txn.isActive()) {
				txn.rollback();
				return null;
			}
		}*/
	}
	
	public AppUser persist() {
		ofy().put(this);
		return this;
	}
	
	/**
     * Auto-increment version # whenever persisted
     */
    @PrePersist
    void onPersist()
    {
        this.version++;
    }
    
    // Getters and setters

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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getId() {
		return username;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}
