package com.fave100.server.domain;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.annotation.Load;

@Entity
public class Follower {
	// TODO: This will only work if we ban : in usernames
	@IgnoreSave public static String ID_SEPARATOR = ":";
	
	@Id private String id;
	@Load private Ref<AppUser> follower;
	@Load private Ref<AppUser> following;
	
	public Follower() {}
	
	public Follower(final String follower, final String following) {
		// We use this weird id to facilitate easy .get() operations
		this.setId(follower + Follower.ID_SEPARATOR + following);
		this.setFollower(Ref.create(Key.create(AppUser.class, follower)));
		this.setFollowing(Ref.create(Key.create(AppUser.class, following)));		
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public Ref<AppUser> getFollower() {
		return follower;
	}

	public void setFollower(final Ref<AppUser> follower) {
		this.follower = follower;
	}

	public Ref<AppUser> getFollowing() {
		return following;
	}

	public void setFollowing(final Ref<AppUser> following) {
		this.following = following;
	}

	
}
