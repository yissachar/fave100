package com.fave100.server.domain;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class APIKey {

	@Id private String id;
	private String key;
	private String secret;

	@SuppressWarnings("unused")
	private APIKey(){};

	public APIKey(final String id, final String key, final String secret) {
		this.id = id;
		this.key = key;
		this.secret = secret;
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(final String key) {
		this.key = key;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(final String secret) {
		this.secret = secret;
	}
}
