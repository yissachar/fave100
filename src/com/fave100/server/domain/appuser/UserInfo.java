package com.fave100.server.domain.appuser;

public class UserInfo {

	private String email;
	private boolean followingPrivate;

	public UserInfo() {
	}

	public UserInfo(final AppUser user) {
		setEmail(user.getEmail());
		setFollowingPrivate(user.isFollowingPrivate());
	}

	/* Getters and Setters */

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public boolean isFollowingPrivate() {
		return followingPrivate;
	}

	public void setFollowingPrivate(final boolean followingPrivate) {
		this.followingPrivate = followingPrivate;
	}
}
