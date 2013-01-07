package com.fave100.server.domain.appuser;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class FacebookID {
	
	@Id private Long facebookID;
	private String username;
	
	@SuppressWarnings("unused")
	private FacebookID() {}
	
	public FacebookID(final Long facebookID, final String username) {
		this.setFacebookID(facebookID);
		this.setUsername(username);
	}
	
	public static FacebookID findFacebookID(final Long id) {
		return ofy().load().type(FacebookID.class).id(id).get();
	}
	

	public Long getFacebookID() {
		return facebookID;
	}

	public void setFacebookID(final Long facebookID) {
		this.facebookID = facebookID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

}
