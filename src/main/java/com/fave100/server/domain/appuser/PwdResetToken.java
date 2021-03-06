package com.fave100.server.domain.appuser;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.Date;
import java.util.UUID;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

/**
 * A token that is emailed to an AppUser to allow them to reset their password
 * 
 * @author yissachar.radcliffe
 * 
 */

@Entity
public class PwdResetToken {

	public final static int EXPIRY_TIME = 1 * 24 * 60 * 60 * 1000;

	@Id private String token;
	@Index private Date expiry;
	@Load private Ref<AppUser> appUser;

	@SuppressWarnings("unused")
	private PwdResetToken() {
	}

	public PwdResetToken(final String username) {
		token = UUID.randomUUID().toString();
		// Expires one day from generation
		final Date today = new Date();
		expiry = new Date(today.getTime() + EXPIRY_TIME);
		setAppUser(Ref.create(Key.create(AppUser.class, username.toLowerCase())));
	}

	public static PwdResetToken findPwdResetToken(final String token) {
		return ofy().load().type(PwdResetToken.class).id(token).now();
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
