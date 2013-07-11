package com.fave100.server.domain.appuser;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Following {

	@Id String user;
	@Index private List<Ref<AppUser>> following = new ArrayList<Ref<AppUser>>();

	@SuppressWarnings("unused")
	private Following() {
	}

	public Following(final String user) {
		this.user = user;
	}

	/* Getters and Setters */

	public String getUser() {
		return user;
	}

	public void setUser(final String user) {
		this.user = user;
	}

	public List<Ref<AppUser>> getFollowing() {
		return following;
	}

	public void setFollowing(final List<Ref<AppUser>> following) {
		this.following = following;
	}

}
