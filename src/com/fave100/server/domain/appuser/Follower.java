package com.fave100.server.domain.appuser;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Follower {

	@Id Long id;
	private Ref<AppUser> user;
	private Ref<AppUser> following;

	@SuppressWarnings("unused")
	private Follower() {
	}

	public Follower(final AppUser user, final String following) {
		setUser(user);
		this.following = Ref.create(Key.create(AppUser.class, following));
	}

	/* Getters and Setters */

	public AppUser getUser() {
		return user.get();
	}

	public void setUser(final AppUser user) {
		this.user = Ref.create(user);
	}

	public AppUser getFollowing() {
		return following.get();
	}

	public void setFollowing(final AppUser following) {
		this.following = Ref.create(following);
	}

}
