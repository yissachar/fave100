package com.fave100.server.domain;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.annotation.Index;

@Entity @Index
public class Follower {
	// TODO: This will only work if we ban : in usernames
	@IgnoreSave public static String ID_SEPARATOR = ":";
	
	@Id private String id;
	private Ref<AppUser> follower;
	private Ref<AppUser> followed;
	
	public Follower() {}
	
	public Follower(String follower, String followed) {
		// We use this weird id to facilitate easy .get() operations
		this.setId(follower + Follower.ID_SEPARATOR + followed);
		this.setFollower(Ref.create(Key.create(AppUser.class, follower)));
		this.setFollowed(Ref.create(Key.create(AppUser.class, followed)));		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Ref<AppUser> getFollower() {
		return follower;
	}

	public void setFollower(Ref<AppUser> follower) {
		this.follower = follower;
	}

	public Ref<AppUser> getFollowed() {
		return followed;
	}

	public void setFollowed(Ref<AppUser> followed) {
		this.followed = followed;
	}
}
