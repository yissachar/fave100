package com.fave100.server.domain.appuser;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class TwitterID {

	@Id private long id;
	private String username;

	public TwitterID() {}

	public TwitterID(final long id, final String username) {
		this.setId(id);
		this.setUsername(username);
	}

	public static TwitterID findTwitterID(final Long id) {
		return ofy().load().type(TwitterID.class).id(id).get();
	}


	// Getters and Setters

	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}
}
