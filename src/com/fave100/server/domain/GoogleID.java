package com.fave100.server.domain;

import javax.persistence.Id;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;

@Entity
public class GoogleID {
	@Id @Unindexed private String googleID;
	@Unindexed private String username;
	
	public GoogleID() {}
	
	public GoogleID(String googleID, String username) {
		this.googleID = googleID;
		this.username = username;
	}
	
	public static final Objectify ofy() {
		return ObjectifyService.begin();
	}
	
	public static GoogleID findGoogleID(String id) {
		return ofy().get(GoogleID.class, id);
	}
	
	public GoogleID persist() {
		ofy().put(this);
		return this;
	}
	
	
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
