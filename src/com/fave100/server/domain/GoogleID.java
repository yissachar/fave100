package com.fave100.server.domain;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class GoogleID {
	@Id private String googleID;
	private String username;
	
	public GoogleID() {}
	
	public GoogleID(String googleID, String username) {
		this.googleID = googleID;
		this.username = username;
	}
		
	public static GoogleID findGoogleID(String id) {
		return ofy().load().type(GoogleID.class).id(id).get();
		//return ofy().get(GoogleID.class, id);		
	}
	
	/*public GoogleID persist() {
		ofy().put(this);
		return this;
	}*/
	
	
	/* Getters and setters */
	
	public String getGoogleID() {
		return googleID;
	}
	public void setGoogleID(String googleID) {
		this.googleID = googleID;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

}
