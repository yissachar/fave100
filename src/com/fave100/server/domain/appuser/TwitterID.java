package com.fave100.server.domain.appuser;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Allows lookup of an AppUser by their TwitterID
 * @author yissachar.radcliffe
 *
 */

@Entity
public class TwitterID {

	@Id private long id;
	private Ref<AppUser> user;

	public TwitterID() {}

	public TwitterID(final long id, final AppUser user) {
		this.setId(id);
		this.setUser(Ref.create(Key.create(AppUser.class, user.getUsername().toLowerCase())));
	}

	public static TwitterID findTwitterID(final Long id) {
		return ofy().load().type(TwitterID.class).id(id).get();
	}


	/* Getters and Setters */

	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public Ref<AppUser> getUser() {
		return user;
	}

	public void setUser(final Ref<AppUser> user) {
		this.user = user;
	}
}
