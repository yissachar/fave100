package com.fave100.server.domain.appuser;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Allows lookup of an AppUser by their FacebookID
 * @author yissachar.radcliffe
 *
 */
@Entity
public class FacebookID {

	@Id private long facebookID;
	private Ref<AppUser> user;

	@SuppressWarnings("unused")
	private FacebookID() {}

	public FacebookID(final Long facebookID, final AppUser user) {
		this.setFacebookID(facebookID);
		this.setUser(Ref.create(Key.create(AppUser.class, user.getUsername())));
	}

	public static FacebookID findFacebookID(final Long id) {
		return ofy().load().type(FacebookID.class).id(id).get();
	}


	/* Getters and Setters */

	public Long getFacebookID() {
		return facebookID;
	}

	public void setFacebookID(final Long facebookID) {
		this.facebookID = facebookID;
	}

	public Ref<AppUser> getUser() {
		return user;
	}

	public void setUser(final Ref<AppUser> user) {
		this.user = user;
	}

}
