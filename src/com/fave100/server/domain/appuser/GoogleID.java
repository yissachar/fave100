package com.fave100.server.domain.appuser;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class GoogleID {
	@Id private String googleID;
	private String username;
	
	public GoogleID() {}
	
	public GoogleID(final String googleID, final String username) {
		this.googleID = googleID;
		this.username = username;
	}
		
	public static GoogleID findGoogleID(final String id) {
		return ofy().load().type(GoogleID.class).id(id).get();
	}
	
	
	/* Getters and setters */
	
	public String getGoogleID() {
		return googleID;
	}
	public void setGoogleID(final String googleID) {
		this.googleID = googleID;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(final String username) {
		this.username = username;
	}

}
