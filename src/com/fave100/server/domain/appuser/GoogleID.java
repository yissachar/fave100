package com.fave100.server.domain.appuser;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Allows lookup of an AppUser by their Google ID
 * 
 * @author yissachar.radcliffe
 * 
 */

@Entity
public class GoogleID {
	@Id private String googleID;
	private Ref<AppUser> user;

	public GoogleID() {
	}

	public GoogleID(final String googleID, final AppUser user) {
		this.googleID = googleID;
		this.setUser(Ref.create(Key.create(AppUser.class, user.getUsername().toLowerCase())));
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

	public Ref<AppUser> getUser() {
		return user;
	}

	public void setUser(final Ref<AppUser> user) {
		this.user = user;
	}

}
