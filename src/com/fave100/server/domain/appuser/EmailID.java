package com.fave100.server.domain.appuser;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Allows lookup of an AppUser by their (lowercase) Email address
 * @author yissachar.radcliffe
 *
 */
@Entity
public class EmailID {

	@Id private String emailID;
	private Ref<AppUser> user;

	@SuppressWarnings("unused")
	private EmailID() {}

	public EmailID(final String emailID, final AppUser user) {
		this.setEmailID(emailID.toLowerCase());
		this.setUser(Ref.create(Key.create(AppUser.class, user.getUsername().toLowerCase())));
	}

	public static EmailID findEmailID(final String id) {
		return ofy().load().type(EmailID.class).id(id.toLowerCase()).get();
	}


	/* Getters and Setters */

	public String getEmailID() {
		return emailID;
	}

	public void setEmailID(final String emailID) {
		this.emailID = emailID.toLowerCase();
	}

	public Ref<AppUser> getUser() {
		return user;
	}

	public void setUser(final Ref<AppUser> user) {
		this.user = user;
	}

}
