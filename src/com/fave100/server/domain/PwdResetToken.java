package com.fave100.server.domain;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.Date;
import java.util.UUID;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;

@Entity
public class PwdResetToken {
	@Id private String token;
	private Date expiry;
	@Load private Ref<AppUser> appUser;

	@SuppressWarnings("unused")
	private PwdResetToken() {}

	public PwdResetToken(final String username) {
		token = UUID.randomUUID().toString();
		// Expires one day from generation
		final Date today = new Date();
		expiry = new Date(today.getTime() + 1 * 24 * 60 * 60 * 1000);
		setAppUser(Ref.create(Key.create(AppUser.class, username)));
	}

	public static PwdResetToken findPwdResetToken(final String token) {
		return ofy().load().type(PwdResetToken.class).id(token).get();
	}



	/* Getters and setters */

	public String getToken() {
		return token;
	}

	public void setToken(final String token) {
		this.token = token;
	}

	public Date getExpiry() {
		return expiry;
	}

	public void setExpiry(final Date expiry) {
		this.expiry = expiry;
	}



	public Ref<AppUser> getAppUser() {
		return appUser;
	}



	public void setAppUser(final Ref<AppUser> appUser) {
		this.appUser = appUser;
	}

}
