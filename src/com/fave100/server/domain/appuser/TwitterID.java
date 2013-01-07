package com.fave100.server.domain.appuser;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class TwitterID {
	
	@Id private long id;
	private String username;
	
	public TwitterID() {}
	
	public TwitterID(long id, String username) {
		this.setId(id);
		this.setUsername(username);
	}
	
	
	// Getters and Setters

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
