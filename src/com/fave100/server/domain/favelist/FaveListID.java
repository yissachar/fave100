package com.fave100.server.domain.favelist;

import com.googlecode.objectify.annotation.Embed;

@Embed
public class FaveListID {

	private String username;
	private String hashtag;

	@SuppressWarnings("unused")
	private FaveListID() {
	}

	public FaveListID(final String username, final String hashtag) {
		this.setUsername(username);
		this.setHashtag(hashtag);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hashtag == null) ? 0 : hashtag.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final FaveListID other = (FaveListID)obj;
		if (hashtag == null) {
			if (other.hashtag != null)
				return false;
		}
		else if (!hashtag.equals(other.hashtag))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		}
		else if (!username.equals(other.username))
			return false;
		return true;
	}

	/* Getters and Setters */

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getHashtag() {
		return hashtag;
	}

	public void setHashtag(final String hashtag) {
		this.hashtag = hashtag;
	}

}
