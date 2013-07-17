package com.fave100.server.domain.appuser;

import java.util.List;

public class FollowingResult {

	private List<AppUser> following;
	// The number of results
	private boolean more;

	@SuppressWarnings("unused")
	private FollowingResult() {
	}

	public FollowingResult(final List<AppUser> following, final boolean more) {
		this.following = following;
		this.more = more;
	}

	/* Getters and Setters */

	public boolean isMore() {
		return more;
	}

	public void setMore(final boolean more) {
		this.more = more;
	}

	public List<AppUser> getFollowing() {
		return following;
	}

	public void setFollowing(final List<AppUser> following) {
		this.following = following;
	}

}
