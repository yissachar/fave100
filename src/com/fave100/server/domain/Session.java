package com.fave100.server.domain;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Serialize;

@Cache
@Entity
public class Session {

	// This is expensive to initialize, keep around for reuse
	private static SecureRandom random = new SecureRandom();

	@Id private String id;

	private Date expires;

	@Serialize private Map<String, Object> attributes = new HashMap<>();

	public Session() {
		id = new BigInteger(130, random).toString(32);
		expires = new Date(new Date().getTime() + 1000 * 60 * 60 * 24);
	}

	public String getId() {
		return id;
	}

	public Date getExpires() {
		return expires;
	}

	public boolean isExpired() {
		return (new Date().after(getExpires()));
	}

	public Object getAttribute(String key) {
		return attributes.get(key);
	}

	public void setAttribute(String key, Object value) {
		attributes.put(key, value);
	}
}
